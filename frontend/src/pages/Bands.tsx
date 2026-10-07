import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

interface Band {
  id: number;
  name: string;
  description: string;
  currentUserRole: "ADMIN" | "MEMBER";
  currentUserInstrument?: string | null;
}

export function Bands() {
  const [bands, setBands] = useState<Band[]>([]);
  const [loading, setLoading] = useState(true);

  // Modal Criar Banda
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [instrument, setInstrument] = useState("");

  // Menu 3 pontinhos nos cartões
  const [openDropdownId, setOpenDropdownId] = useState<number | null>(null);

  // Modal Editar Banda
  const [editingBand, setEditingBand] = useState<Band | null>(null);
  const [editName, setEditName] = useState("");
  const [editDescription, setEditDescription] = useState("");

  // Modal Apagar Banda
  const [deletingBand, setDeletingBand] = useState<Band | null>(null);

  const navigate = useNavigate();

  const fetchBands = async () => {
    try {
      const response = await api.get("/bands");
      setBands(response.data);
    } catch (err) {
      console.error("Erro ao carregar bandas:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBands();
  }, []);

  // Fechar dropdowns ao clicar fora
  useEffect(() => {
    const handleClickOutside = () => setOpenDropdownId(null);
    window.addEventListener("click", handleClickOutside);
    return () => window.removeEventListener("click", handleClickOutside);
  }, []);

  const handleCreateBand = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.post("/bands", {
        name,
        description,
        instrument: instrument || null,
      });
      setName("");
      setDescription("");
      setInstrument("");
      setIsModalOpen(false);
      fetchBands();
      navigate(`/bandas/${response.data.id}`);
    } catch (err) {
      console.error("Erro ao criar banda:", err);
    }
  };

  // Abrir modal Editar
  const handleOpenEdit = (band: Band, e: React.MouseEvent) => {
    e.stopPropagation();
    setOpenDropdownId(null);
    setEditingBand(band);
    setEditName(band.name);
    setEditDescription(band.description || "");
  };

  const handleUpdateBand = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingBand || !editName) return;

    try {
      await api.put(`/bands/${editingBand.id}`, {
        name: editName,
        description: editDescription,
      });
      setEditingBand(null);
      fetchBands();
    } catch (err) {
      console.error("Erro ao atualizar banda:", err);
    }
  };

  // Abrir modal Apagar
  const handleOpenDelete = (band: Band, e: React.MouseEvent) => {
    e.stopPropagation();
    setOpenDropdownId(null);
    setDeletingBand(band);
  };

  const handleDeleteBand = async () => {
    if (!deletingBand) return;

    try {
      await api.delete(`/bands/${deletingBand.id}`);
      setDeletingBand(null);
      fetchBands();
    } catch (err) {
      console.error("Erro ao apagar banda:", err);
    }
  };

  return (
    <div className="space-y-8">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-bold text-slate-100">
            As Minhas Bandas
          </h2>
          <p className="text-slate-400 text-sm">
            Seleciona uma banda para gerir o seu repertório e alinhamentos de
            concertos
          </p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium rounded-lg transition"
        >
          + Nova Banda
        </button>
      </div>

      {loading ? (
        <p className="text-slate-400 text-center py-12">A carregar bandas...</p>
      ) : bands.length === 0 ? (
        <div className="p-12 border border-dashed border-slate-700 rounded-2xl text-center">
          <p className="text-slate-400">
            Ainda não tens nenhuma banda registada.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {bands.map((band) => (
            <div
              key={band.id}
              onClick={() => navigate(`/bandas/${band.id}`)}
              className="relative bg-slate-900 border border-slate-800 hover:border-indigo-500/50 p-6 rounded-2xl cursor-pointer transition group flex flex-col justify-between"
            >
              <div>
                <div className="flex justify-between items-start mb-2">
                  <h3 className="text-xl font-bold text-slate-100 group-hover:text-indigo-400 transition pr-2">
                    {band.name}
                  </h3>

                  {/* Menu 3 Pontinhos (só admin) */}
                  {band.currentUserRole === "ADMIN" && (
                    <div className="relative">
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          setOpenDropdownId(
                            openDropdownId === band.id ? null : band.id,
                          );
                        }}
                        className="p-1.5 text-slate-400 hover:text-slate-200 hover:bg-slate-800 rounded-lg transition"
                        title="Opções da Banda"
                      >
                        <svg
                          className="w-4 h-4 fill-current"
                          viewBox="0 0 24 24"
                        >
                          <path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z" />
                        </svg>
                      </button>

                      {openDropdownId === band.id && (
                        <div
                          onClick={(e) => e.stopPropagation()}
                          className="absolute right-0 mt-1 w-36 bg-slate-800 border border-slate-700 rounded-xl shadow-xl z-20 py-1 text-xs"
                        >
                          <button
                            type="button"
                            onClick={(e) => handleOpenEdit(band, e)}
                            className="w-full text-left px-3 py-2 text-slate-200 hover:bg-slate-700/80 transition"
                          >
                            Editar
                          </button>
                          <button
                            type="button"
                            onClick={(e) => handleOpenDelete(band, e)}
                            className="w-full text-left px-3 py-2 text-red-400 hover:bg-slate-700/80 transition"
                          >
                            Apagar
                          </button>
                        </div>
                      )}
                    </div>
                  )}
                </div>

                <p className="text-slate-400 text-sm line-clamp-2">
                  {band.description || "Sem descrição definida"}
                </p>

                <div className="mt-3 flex items-center gap-2 flex-wrap text-xs">
                  <span
                    className={`px-2 py-0.5 rounded-md text-[10px] font-bold uppercase tracking-wider border ${
                      band.currentUserRole === "ADMIN"
                        ? "bg-indigo-500/15 text-indigo-300 border-indigo-500/40"
                        : "bg-slate-800 text-slate-400 border-slate-700"
                    }`}
                  >
                    {band.currentUserRole === "ADMIN" ? "Admin" : "Membro"}
                  </span>
                  {band.currentUserInstrument && (
                    <span className="px-2 py-0.5 bg-slate-800 border border-slate-700 rounded text-indigo-300">
                      {band.currentUserInstrument}
                    </span>
                  )}
                </div>
              </div>

              <div className="mt-6 pt-4 border-t border-slate-800/80 flex justify-between items-center text-xs text-slate-500">
                <span>
                  {band.currentUserRole === "ADMIN"
                    ? "Gerir Repertório & Setlists"
                    : "Ver Repertório & Setlists"}
                </span>
                <span className="text-indigo-400 group-hover:translate-x-1 transition-transform">
                  Entrar →
                </span>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal Criar Banda */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              Criar Nova Banda
            </h3>
            <form onSubmit={handleCreateBand} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Nome da Banda
                </label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Ex: Smoodies"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Descrição / Género
                </label>
                <input
                  type="text"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Ex: Soul, Funk & Pop"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  A Tua Função (opcional)
                </label>
                <input
                  type="text"
                  value={instrument}
                  onChange={(e) => setInstrument(e.target.value)}
                  placeholder="Ex: Baixo, Voz, Teclas"
                  maxLength={100}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 text-slate-400 hover:text-slate-200 text-sm font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium rounded-lg transition"
                >
                  Criar Banda
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Editar Banda */}
      {editingBand && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              Editar Banda
            </h3>
            <form onSubmit={handleUpdateBand} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Nome da Banda
                </label>
                <input
                  type="text"
                  required
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Descrição / Género
                </label>
                <input
                  type="text"
                  value={editDescription}
                  onChange={(e) => setEditDescription(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setEditingBand(null)}
                  className="px-4 py-2 text-slate-400 hover:text-slate-200 text-sm font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium rounded-lg transition"
                >
                  Guardar Alterações
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Apagar Banda */}
      {deletingBand && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md space-y-4">
            <h3 className="text-xl font-bold text-slate-100">Apagar Banda</h3>
            <p className="text-sm text-slate-300">
              Tens a certeza que desejas apagar a banda{" "}
              <span className="font-semibold text-slate-100">
                "{deletingBand.name}"
              </span>
              ?
            </p>
            <p className="text-xs text-slate-500">
              Esta ação apaga a banda e os seus alinhamentos associados. As
              músicas no catálogo geral não serão eliminadas.
            </p>
            <div className="flex justify-end space-x-3 pt-2">
              <button
                type="button"
                onClick={() => setDeletingBand(null)}
                className="px-4 py-2 text-slate-400 hover:text-slate-200 text-sm font-medium"
              >
                Cancelar
              </button>
              <button
                type="button"
                onClick={handleDeleteBand}
                className="px-4 py-2 bg-red-600 hover:bg-red-500 text-white text-sm font-medium rounded-lg transition"
              >
                Apagar Definitivamente
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

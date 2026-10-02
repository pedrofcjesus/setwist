import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

interface Band {
  id: number;
  name: string;
  description: string;
}

export function Bands() {
  const [bands, setBands] = useState<Band[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
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

  const handleCreateBand = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.post("/bands", { name, description });
      setName("");
      setDescription("");
      setIsModalOpen(false);
      fetchBands();
      navigate(`/bandas/${response.data.id}`);
    } catch (err) {
      console.error("Erro ao criar banda:", err);
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
              className="bg-slate-900 border border-slate-800 hover:border-indigo-500/50 p-6 rounded-2xl cursor-pointer transition group flex flex-col justify-between"
            >
              <div>
                <div className="flex justify-between items-start mb-2">
                  <h3 className="text-xl font-bold text-slate-100 group-hover:text-indigo-400 transition">
                    {band.name}
                  </h3>
                  <span className="text-xs bg-indigo-600/20 text-indigo-300 border border-indigo-500/30 px-2 py-1 rounded">
                    Projeto
                  </span>
                </div>
                <p className="text-slate-400 text-sm line-clamp-2">
                  {band.description || "Sem descrição definida"}
                </p>
              </div>

              <div className="mt-6 pt-4 border-t border-slate-800/80 flex justify-between items-center text-xs text-slate-500">
                <span>Gerir Repertório & Setlists</span>
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
    </div>
  );
}

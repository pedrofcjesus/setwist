import React, { useEffect, useState } from "react";
import api from "../api/axios";

interface Song {
  id: number;
  title: string;
  artist: string;
  bands?: string[];
  bandsCount?: number;
  setlistsCount?: number;
  setlists?: { id: number; name: string }[];
}

interface UsageData {
  bandsCount: number;
  setlistsCount: number;
  bandsList: string[];
  setlistsList: string[];
}

type SortField = "title" | "artist";
type SortOrder = "asc" | "desc";

export function Songs() {
  const [songs, setSongs] = useState<Song[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");

  // Estados de Ordenação
  const [sortField, setSortField] = useState<SortField>("title");
  const [sortOrder, setSortOrder] = useState<SortOrder>("asc");

  // Form de Criar/Editar Música
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingSong, setEditingSong] = useState<Song | null>(null);
  const [title, setTitle] = useState("");
  const [artist, setArtist] = useState("");

  // Modal de Confirmação de Remoção com Aviso
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [songToDelete, setSongToDelete] = useState<Song | null>(null);
  const [usageData, setUsageData] = useState<UsageData | null>(null);
  const [isCheckingUsage, setIsCheckingUsage] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);

  const fetchSongs = async () => {
    try {
      const response = await api.get("/songs");
      setSongs(response.data);
    } catch (err) {
      console.error("Erro ao carregar catálogo de músicas:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSongs();
  }, []);

  const handleOpenCreateModal = () => {
    setEditingSong(null);
    setTitle("");
    setArtist("");
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (song: Song) => {
    setEditingSong(song);
    setTitle(song.title);
    setArtist(song.artist);
    setIsModalOpen(true);
  };

  const handleSubmitSong = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingSong) {
        await api.put(`/songs/${editingSong.id}`, { title, artist });
      } else {
        await api.post("/songs", { title, artist });
      }

      setTitle("");
      setArtist("");
      setEditingSong(null);
      setIsModalOpen(false);
      fetchSongs();
    } catch (err) {
      console.error("Erro ao guardar música:", err);
    }
  };

  // Abrir modal de remoção e verificar associações (bandas e setlists)
  const handleOpenDeleteModal = async (song: Song) => {
    setSongToDelete(song);
    setUsageData(null);
    setDeleteError(null);
    setIsCheckingUsage(true);
    setIsDeleteModalOpen(true);

    try {
      const res = await api.get(`/songs/${song.id}`);
      const data = res.data;

      const bandsList: string[] = Array.isArray(data.bands)
        ? data.bands
            .map((b: any) => (typeof b === "string" ? b : b?.name))
            .filter(Boolean)
        : [];

      const setlists =
        data.setlists || data.setlistSongs?.map((s: any) => s.setlist) || [];
      const setlistsList = setlists
        .map((s: any) => (typeof s === "string" ? s : s?.name))
        .filter(Boolean);

      setUsageData({
        bandsCount: data.bandsCount ?? bandsList.length,
        setlistsCount: data.setlistsCount ?? setlistsList.length,
        bandsList,
        setlistsList,
      });
    } catch (err) {
      console.error("Erro ao verificar utilização da música:", err);
      setUsageData({
        bandsCount: song.bands?.length || song.bandsCount || 0,
        setlistsCount: song.setlistsCount || 0,
        bandsList: song.bands || [],
        setlistsList: song.setlists?.map((s) => s.name) || [],
      });
    } finally {
      setIsCheckingUsage(false);
    }
  };

  const handleConfirmDelete = async () => {
    if (!songToDelete) return;

    try {
      await api.delete(`/songs/${songToDelete.id}`);
      setIsDeleteModalOpen(false);
      setSongToDelete(null);
      fetchSongs();
    } catch (err: any) {
      console.error("Erro ao apagar música:", err);

      const rawError = err.response?.data?.message || "";

      if (
        rawError.includes("foreign key constraint") ||
        rawError.includes("CONSTRAINT") ||
        rawError.includes("band_repertoire")
      ) {
        setDeleteError(
          "Esta música não pode ser apagada porque está associada ao repertório de uma ou mais bandas. Remove-a primeiro das bandas associadas.",
        );
      } else {
        setDeleteError(
          rawError ||
            "Ocorreu um erro ao tentar apagar a música. Tenta novamente.",
        );
      }
    }
  };

  const handleSort = (field: SortField) => {
    if (sortField === field) {
      setSortOrder(sortOrder === "asc" ? "desc" : "asc");
    } else {
      setSortField(field);
      setSortOrder("asc");
    }
  };

  const processedSongs = songs
    .filter(
      (song) =>
        song.title.toLowerCase().includes(search.toLowerCase()) ||
        song.artist.toLowerCase().includes(search.toLowerCase()),
    )
    .sort((a, b) => {
      let comparison = 0;
      if (sortField === "title") {
        comparison = a.title.localeCompare(b.title);
      } else if (sortField === "artist") {
        comparison = a.artist.localeCompare(b.artist);
      }
      return sortOrder === "asc" ? comparison : -comparison;
    });

  const renderSortIcon = (field: SortField) => {
    if (sortField !== field)
      return <span className="text-slate-600 ml-1">↕</span>;
    return sortOrder === "asc" ? (
      <span className="text-indigo-400 ml-1">↑</span>
    ) : (
      <span className="text-indigo-400 ml-1">↓</span>
    );
  };

  return (
    <div className="space-y-6">
      {/* Cabeçalho */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold text-slate-100">
            Catálogo de Músicas
          </h2>
          <p className="text-slate-400 text-sm">
            Base de dados global de temas disponíveis
          </p>
        </div>
        <button
          onClick={handleOpenCreateModal}
          className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium rounded-lg transition"
        >
          + Nova Música
        </button>
      </div>

      {/* Barra de Pesquisa e Filtros */}
      <div className="flex justify-between items-center bg-slate-900 p-4 rounded-xl border border-slate-800">
        <input
          type="text"
          placeholder="Pesquisar por título ou artista..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full max-w-md px-4 py-2 bg-slate-800 border border-slate-700 rounded-lg text-slate-100 placeholder-slate-500 focus:outline-none focus:border-indigo-500 text-sm"
        />
        <span className="text-xs text-slate-400 hidden sm:inline-block">
          {processedSongs.length} tema(s) encontrado(s)
        </span>
      </div>

      {/* Tabela de Músicas */}
      {loading ? (
        <p className="text-slate-400 text-center py-8">
          A carregar catálogo...
        </p>
      ) : processedSongs.length === 0 ? (
        <div className="p-8 border border-dashed border-slate-700 rounded-xl text-center">
          <p className="text-slate-400">Nenhuma música encontrada.</p>
        </div>
      ) : (
        <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="bg-slate-800/50 border-b border-slate-800 text-slate-400 text-xs uppercase tracking-wider select-none">
                  <th
                    onClick={() => handleSort("title")}
                    className="py-3.5 px-4 cursor-pointer hover:text-slate-200 transition"
                  >
                    Título {renderSortIcon("title")}
                  </th>
                  <th
                    onClick={() => handleSort("artist")}
                    className="py-3.5 px-4 cursor-pointer hover:text-slate-200 transition"
                  >
                    Artista {renderSortIcon("artist")}
                  </th>
                  <th className="py-3.5 px-4">Bandas</th>
                  <th className="py-3.5 px-4 text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {processedSongs.map((song) => (
                  <tr
                    key={song.id}
                    className="hover:bg-slate-800/40 transition text-slate-200"
                  >
                    <td className="py-3 px-4 font-semibold text-slate-100">
                      {song.title}
                    </td>
                    <td className="py-3 px-4 text-slate-400">{song.artist}</td>

                    {/* Indicador Visual de Bandas */}
                    <td className="py-3 px-4 text-slate-300">
                      {!song.bands || song.bands.length === 0 ? (
                        <span className="text-slate-600 font-medium">-</span>
                      ) : song.bands.length === 1 ? (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-md text-xs font-medium bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
                          {song.bands[0]}
                        </span>
                      ) : (
                        <span
                          title={song.bands.join(", ")}
                          className="inline-flex items-center px-2.5 py-0.5 rounded-md text-xs font-medium bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 cursor-help"
                        >
                          {song.bands[0]}
                          <span className="ml-1.5 font-bold text-indigo-300">
                            +{song.bands.length - 1}
                          </span>
                        </span>
                      )}
                    </td>

                    <td className="py-3 px-4 text-right space-x-3">
                      <button
                        onClick={() => handleOpenEditModal(song)}
                        className="text-xs text-indigo-400 hover:text-indigo-300 transition"
                      >
                        Editar
                      </button>
                      <button
                        onClick={() => handleOpenDeleteModal(song)}
                        className="text-xs text-red-400 hover:text-red-300 transition"
                      >
                        Apagar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Modal Criar / Editar Música */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              {editingSong ? "Editar Música" : "Adicionar Música ao Catálogo"}
            </h3>
            <form onSubmit={handleSubmitSong} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Título
                </label>
                <input
                  type="text"
                  required
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Ex: Superstition"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Artista / Banda Original
                </label>
                <input
                  type="text"
                  required
                  value={artist}
                  onChange={(e) => setArtist(e.target.value)}
                  placeholder="Ex: Stevie Wonder"
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
                  {editingSong ? "Guardar Alterações" : "Guardar"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Confirmação de Remoção com Aviso de Associações */}
      {isDeleteModalOpen && songToDelete && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md space-y-4">
            <h3 className="text-xl font-bold text-slate-100">Apagar Música</h3>

            {isCheckingUsage ? (
              <p className="text-sm text-slate-400 animate-pulse py-2">
                A verificar associações com bandas e setlists...
              </p>
            ) : (
              <>
                <p className="text-sm text-slate-300">
                  Tens a certeza que desejas apagar{" "}
                  <span className="font-semibold text-slate-100">
                    "{songToDelete.title}"
                  </span>{" "}
                  de{" "}
                  <span className="font-semibold text-slate-100">
                    {songToDelete.artist}
                  </span>
                  ?
                </p>

                {usageData &&
                (usageData.bandsCount > 0 || usageData.setlistsCount > 0) ? (
                  <div className="bg-amber-500/10 border border-amber-500/30 p-4 rounded-xl space-y-2 text-xs text-amber-200">
                    <div className="font-bold flex items-center gap-1.5 text-amber-400">
                      <span>⚠️</span> Aviso de Associação
                    </div>
                    <p>Esta música está atualmente atribuída a:</p>
                    <ul className="list-disc list-inside space-y-1 font-mono text-amber-300/90">
                      {usageData.bandsCount > 0 && (
                        <li>
                          {usageData.bandsCount} banda(s)
                          {usageData.bandsList.length > 0 &&
                            ` (${usageData.bandsList.join(", ")})`}
                        </li>
                      )}
                      {usageData.setlistsCount > 0 && (
                        <li>
                          {usageData.setlistsCount} setlist(s)
                          {usageData.setlistsList.length > 0 &&
                            ` (${usageData.setlistsList.join(", ")})`}
                        </li>
                      )}
                    </ul>
                    <p className="pt-1 text-amber-400/80 italic">
                      Ao apagar do catálogo global, ela será também removida
                      desses repertórios e alinhamentos.
                    </p>
                  </div>
                ) : (
                  <p className="text-xs text-slate-500">
                    Esta ação não pode ser desfeita.
                  </p>
                )}

                {deleteError && (
                  <div className="p-3 bg-red-500/10 border border-red-500/30 text-red-400 text-xs rounded-lg">
                    {deleteError}
                  </div>
                )}
              </>
            )}

            <div className="flex justify-end space-x-3 pt-2">
              <button
                type="button"
                onClick={() => {
                  setIsDeleteModalOpen(false);
                  setSongToDelete(null);
                }}
                className="px-4 py-2 text-slate-400 hover:text-slate-200 text-sm font-medium"
              >
                Cancelar
              </button>
              <button
                type="button"
                disabled={isCheckingUsage}
                onClick={handleConfirmDelete}
                className="px-4 py-2 bg-red-600 hover:bg-red-500 disabled:opacity-50 text-white text-sm font-medium rounded-lg transition"
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

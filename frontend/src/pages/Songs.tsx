import React, { useEffect, useState } from "react";
import api from "../api/axios";

interface Song {
  id: number;
  title: string;
  artist: string;
}

type SortField = "id" | "title" | "artist";
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
  const [title, setTitle] = useState("");
  const [artist, setArtist] = useState("");

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

  const handleCreateSong = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post("/songs", { title, artist });
      setTitle("");
      setArtist("");
      setIsModalOpen(false);
      fetchSongs();
    } catch (err) {
      console.error("Erro ao criar música:", err);
    }
  };

  // Alterar campo ou direção de ordenação
  const handleSort = (field: SortField) => {
    if (sortField === field) {
      setSortOrder(sortOrder === "asc" ? "desc" : "asc");
    } else {
      setSortField(field);
      setSortOrder("asc");
    }
  };

  // Filtragem e Ordenação dos dados
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
      } else if (sortField === "id") {
        comparison = a.id - b.id;
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
          onClick={() => setIsModalOpen(true)}
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
                    onClick={() => handleSort("id")}
                    className="py-3.5 px-4 cursor-pointer hover:text-slate-200 transition"
                  >
                    # {renderSortIcon("id")}
                  </th>
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
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {processedSongs.map((song) => (
                  <tr
                    key={song.id}
                    className="hover:bg-slate-800/40 transition text-slate-200"
                  >
                    <td className="py-3 px-4 font-mono text-xs text-slate-500">
                      {song.id}
                    </td>
                    <td className="py-3 px-4 font-semibold text-slate-100">
                      {song.title}
                    </td>
                    <td className="py-3 px-4 text-slate-400">{song.artist}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Modal Criar Música */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              Adicionar Música ao Catálogo
            </h3>
            <form onSubmit={handleCreateSong} className="space-y-4">
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
                  Guardar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

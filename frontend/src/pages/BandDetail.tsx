import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/axios";

interface Song {
  id: number;
  title: string;
  artist: string;
}

interface RepertoireItem {
  id: number;
  songId?: number;
  title?: string;
  artist?: string;
  songKey: string;
  bpm: number;
  notes: string;
  song?: Song;
}

interface Setlist {
  id: number;
  name: string;
  description: string;
  totalSongs: number;
  totalDurationSeconds: number;
}

export function BandDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [band, setBand] = useState<{
    id: number;
    name: string;
    description: string;
  } | null>(null);
  const [repertoire, setRepertoire] = useState<RepertoireItem[]>([]);
  const [suggestions, setSuggestions] = useState<Song[]>([]);
  const [setlists, setSetlists] = useState<Setlist[]>([]);
  const [globalSongs, setGlobalSongs] = useState<Song[]>([]);
  const [loading, setLoading] = useState(true);

  // Modais de Repertório e Setlist
  const [isRepertoireModalOpen, setIsRepertoireModalOpen] = useState(false);
  const [isSetlistModalOpen, setIsSetlistModalOpen] = useState(false);

  // Editar Música do Repertório
  const [editingRepertoireItem, setEditingRepertoireItem] =
    useState<RepertoireItem | null>(null);
  const [editSongTitle, setEditSongTitle] = useState("");
  const [editSongArtist, setEditSongArtist] = useState("");
  const [editSongKey, setEditSongKey] = useState("");
  const [editSongBpm, setEditSongBpm] = useState<number | "">("");
  const [editSongNotes, setEditSongNotes] = useState("");

  // Remover Música do Repertório (Modal de Confirmação com Aviso)
  const [deletingRepertoireItem, setDeletingRepertoireItem] =
    useState<RepertoireItem | null>(null);

  // Sugestões: adicionar
  const [isSuggestionModalOpen, setIsSuggestionModalOpen] = useState(false);
  const [isCreatingNewSuggestion, setIsCreatingNewSuggestion] = useState(false);
  const [selectedSuggestionSongId, setSelectedSuggestionSongId] = useState<
    number | ""
  >("");
  const [newSuggestionTitle, setNewSuggestionTitle] = useState("");
  const [newSuggestionArtist, setNewSuggestionArtist] = useState("");

  // Sugestões: editar
  const [editingSuggestion, setEditingSuggestion] = useState<Song | null>(null);
  const [editSuggestionTitle, setEditSuggestionTitle] = useState("");
  const [editSuggestionArtist, setEditSuggestionArtist] = useState("");

  // Editar e Apagar Banda
  const [isEditBandModalOpen, setIsEditBandModalOpen] = useState(false);
  const [editBandName, setEditBandName] = useState("");
  const [editBandDesc, setEditBandDesc] = useState("");
  const [isDeleteBandModalOpen, setIsDeleteBandModalOpen] = useState(false);

  // Menu de opções nas Setlists
  const [openDropdownId, setOpenDropdownId] = useState<number | null>(null);

  // Modal Editar Setlist
  const [editingSetlist, setEditingSetlist] = useState<Setlist | null>(null);
  const [editSetlistName, setEditSetlistName] = useState("");
  const [editSetlistDesc, setEditSetlistDesc] = useState("");

  // Modal Apagar Setlist
  const [deletingSetlist, setDeletingSetlist] = useState<Setlist | null>(null);

  // Alternar entre selecionar existente ou criar nova
  const [isCreatingNewSong, setIsCreatingNewSong] = useState(false);

  // Form Repertório / Nova Música
  const [selectedSongId, setSelectedSongId] = useState<number | "">("");
  const [newTitle, setNewTitle] = useState("");
  const [newArtist, setNewArtist] = useState("");
  const [songKey, setSongKey] = useState("");
  const [bpm, setBpm] = useState<number | "">("");
  const [notes, setNotes] = useState("");

  // Form Criar Setlist
  const [setlistName, setSetlistName] = useState("");
  const [setlistDesc, setSetlistDesc] = useState("");

  const fetchData = async () => {
    try {
      const [bandRes, repRes, suggestionsRes, setlistRes, globalRes] =
        await Promise.all([
          api.get(`/bands/${id}`),
          api.get(`/bands/${id}/repertoire`),
          api.get(`/bands/${id}/suggestions`),
          api.get(`/setlists`),
          api.get(`/songs`),
        ]);

      setBand(bandRes.data);
      setRepertoire(repRes.data);
      setSuggestions(suggestionsRes.data);
      setSetlists(
        setlistRes.data.filter((s: any) => s.band?.id === Number(id)),
      );
      setGlobalSongs(globalRes.data);
    } catch (err) {
      console.error("Erro ao carregar detalhes da banda:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [id]);

  useEffect(() => {
    const handleClickOutside = () => setOpenDropdownId(null);
    window.addEventListener("click", handleClickOutside);
    return () => window.removeEventListener("click", handleClickOutside);
  }, []);

  // Editar Banda
  const handleOpenEditBandModal = () => {
    if (!band) return;
    setEditBandName(band.name);
    setEditBandDesc(band.description || "");
    setIsEditBandModalOpen(true);
  };

  const handleUpdateBand = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!band || !editBandName) return;

    try {
      await api.put(`/bands/${id}`, {
        name: editBandName,
        description: editBandDesc,
      });
      setIsEditBandModalOpen(false);
      fetchData();
    } catch (err) {
      console.error("Erro ao atualizar banda:", err);
    }
  };

  // Apagar Banda
  const handleDeleteBand = async () => {
    if (!band) return;

    try {
      await api.delete(`/bands/${id}`);
      setIsDeleteBandModalOpen(false);
      navigate("/bandas");
    } catch (err) {
      console.error("Erro ao apagar banda:", err);
    }
  };

  // Abrir Modal de Edição de Música no Repertório
  const handleOpenEditRepertoire = (item: RepertoireItem) => {
    setEditingRepertoireItem(item);
    setEditSongTitle(item.title || item.song?.title || "");
    setEditSongArtist(item.artist || item.song?.artist || "");
    setEditSongKey(item.songKey || "");
    setEditSongBpm(item.bpm || "");
    setEditSongNotes(item.notes || "");
  };

  // Guardar Edição de Música do Repertório (Global + Banda)
  const handleUpdateRepertoireItem = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingRepertoireItem) return;

    try {
      const targetSongId =
        editingRepertoireItem.songId ?? editingRepertoireItem.song?.id;

      // 1. Atualizar dados globais da música caso exista ID válido
      if (targetSongId) {
        await api.put(`/songs/${targetSongId}`, {
          title: editSongTitle,
          artist: editSongArtist || "Desconhecido",
        });
      }

      // 2. Atualizar dados do repertório da banda
      await api.put(`/bands/${id}/repertoire/${editingRepertoireItem.id}`, {
        songKey: editSongKey,
        bpm: editSongBpm ? Number(editSongBpm) : null,
        notes: editSongNotes,
      });

      setEditingRepertoireItem(null);
      fetchData();
    } catch (err: any) {
      console.error("Erro ao atualizar música do repertório:", err);
      alert(
        "Não foi possível guardar as alterações: " +
          (err.response?.data?.message || err.message || "Erro de servidor."),
      );
    }
  };

  const handleAddSongToRepertoire = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      let targetSongId = selectedSongId;

      if (isCreatingNewSong) {
        if (!newTitle) return;
        const songRes = await api.post("/songs", {
          title: newTitle,
          artist: newArtist || "Desconhecido",
        });
        targetSongId = songRes.data.id;
      }

      if (!targetSongId) return;

      await api.post(`/bands/${id}/repertoire`, {
        songId: Number(targetSongId),
        songKey,
        bpm: bpm ? Number(bpm) : null,
        notes,
      });

      setSelectedSongId("");
      setNewTitle("");
      setNewArtist("");
      setSongKey("");
      setBpm("");
      setNotes("");
      setIsCreatingNewSong(false);
      setIsRepertoireModalOpen(false);
      fetchData();
    } catch (err) {
      console.error("Erro ao adicionar ao repertório:", err);
    }
  };

  // Confirmar e Apagar Música do Repertório (mesmo que esteja em setlists)
  const handleConfirmDeleteRepertoireItem = async () => {
    if (!deletingRepertoireItem) return;

    try {
      await api.delete(`/bands/${id}/repertoire/${deletingRepertoireItem.id}`);
      setDeletingRepertoireItem(null);
      fetchData();
    } catch (err: any) {
      console.error("Erro ao remover do repertório:", err);
      alert(
        "Não foi possível remover a música: " +
          (err.response?.data?.message || err.message || "Erro de servidor."),
      );
    }
  };

  // --- SUGESTÕES ---

  const handleOpenSuggestionModal = () => {
    setIsCreatingNewSuggestion(false);
    setSelectedSuggestionSongId("");
    setNewSuggestionTitle("");
    setNewSuggestionArtist("");
    setIsSuggestionModalOpen(true);
  };

  const handleAddSuggestion = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      let targetSongId = selectedSuggestionSongId;

      if (isCreatingNewSuggestion) {
        if (!newSuggestionTitle) return;
        const songRes = await api.post("/songs", {
          title: newSuggestionTitle,
          artist: newSuggestionArtist || "Desconhecido",
        });
        targetSongId = songRes.data.id;
      }

      if (!targetSongId) return;

      await api.post(`/bands/${id}/suggestions/${Number(targetSongId)}`);

      setSelectedSuggestionSongId("");
      setNewSuggestionTitle("");
      setNewSuggestionArtist("");
      setIsCreatingNewSuggestion(false);
      setIsSuggestionModalOpen(false);
      fetchData();
    } catch (err: any) {
      console.error("Erro ao adicionar sugestão:", err);
      alert(
        "Não foi possível adicionar a sugestão: " +
          (err.response?.data?.message || err.message || "Erro de servidor."),
      );
    }
  };

  const handleOpenEditSuggestion = (song: Song) => {
    setEditingSuggestion(song);
    setEditSuggestionTitle(song.title);
    setEditSuggestionArtist(song.artist || "");
  };

  const handleUpdateSuggestion = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingSuggestion || !editSuggestionTitle) return;

    try {
      await api.put(`/songs/${editingSuggestion.id}`, {
        title: editSuggestionTitle,
        artist: editSuggestionArtist || "Desconhecido",
      });
      setEditingSuggestion(null);
      fetchData();
    } catch (err: any) {
      console.error("Erro ao atualizar sugestão:", err);
      alert(
        "Não foi possível guardar as alterações: " +
          (err.response?.data?.message || err.message || "Erro de servidor."),
      );
    }
  };

  const handleRemoveSuggestion = async (songId: number) => {
    try {
      await api.delete(`/bands/${id}/suggestions/${songId}`);
      fetchData();
    } catch (err: any) {
      console.error("Erro ao retirar sugestão:", err);
      alert(
        "Não foi possível retirar a sugestão: " +
          (err.response?.data?.message || err.message || "Erro de servidor."),
      );
    }
  };

  const handlePromoteSuggestion = async (songId: number) => {
    try {
      await api.post(`/bands/${id}/repertoire/${songId}`);
      fetchData();
    } catch (err: any) {
      console.error("Erro ao promover sugestão:", err);
      alert(
        "Não foi possível promover a música: " +
          (err.response?.data?.message || err.message || "Erro de servidor."),
      );
    }
  };

  const handleCreateSetlist = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await api.post("/setlists", {
        name: setlistName,
        description: setlistDesc,
        bandId: Number(id),
      });
      setIsSetlistModalOpen(false);
      setSetlistName("");
      setSetlistDesc("");
      navigate(`/setlists/${res.data.id}`);
    } catch (err) {
      console.error("Erro ao criar setlist:", err);
    }
  };

  const handleOpenEditSetlist = (s: Setlist, e: React.MouseEvent) => {
    e.stopPropagation();
    setOpenDropdownId(null);
    setEditingSetlist(s);
    setEditSetlistName(s.name);
    setEditSetlistDesc(s.description || "");
  };

  const handleUpdateSetlist = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingSetlist || !editSetlistName) return;

    try {
      await api.put(`/setlists/${editingSetlist.id}`, {
        name: editSetlistName,
        description: editSetlistDesc,
        bandId: Number(id),
      });
      setEditingSetlist(null);
      fetchData();
    } catch (err) {
      console.error("Erro ao atualizar setlist:", err);
    }
  };

  const handleOpenDeleteSetlist = (s: Setlist, e: React.MouseEvent) => {
    e.stopPropagation();
    setOpenDropdownId(null);
    setDeletingSetlist(s);
  };

  const handleDeleteSetlist = async () => {
    if (!deletingSetlist) return;

    try {
      await api.delete(`/setlists/${deletingSetlist.id}`);
      setDeletingSetlist(null);
      fetchData();
    } catch (err) {
      console.error("Erro ao apagar setlist:", err);
    }
  };

  if (loading)
    return (
      <p className="text-slate-400 text-center py-12">
        A carregar detalhes da banda...
      </p>
    );
  if (!band)
    return (
      <p className="text-slate-400 text-center py-12">Banda não encontrada.</p>
    );

  // Músicas do catálogo que ainda não estão no pool nem nas sugestões desta banda
  const repertoireSongIds = repertoire.map((r) => r.songId ?? r.song?.id);
  const suggestionSongIds = suggestions.map((s) => s.id);
  const songsAvailableForSuggestion = globalSongs.filter(
    (s) =>
      !repertoireSongIds.includes(s.id) && !suggestionSongIds.includes(s.id),
  );

  return (
    <div className="space-y-10">
      {/* CABEÇALHO DA BANDA */}
      <div className="flex justify-between items-start">
        <div>
          <button
            onClick={() => navigate("/bandas")}
            className="text-xs text-indigo-400 hover:underline mb-2 inline-block"
          >
            ← Voltar às Bandas
          </button>
          <h2 className="text-3xl font-bold text-slate-100">{band.name}</h2>
          <p className="text-slate-400 text-sm mt-1">
            {band.description || "Sem descrição"}
          </p>
        </div>

        {/* Botões Editar / Apagar Banda */}
        <div className="flex space-x-2 pt-1">
          <button
            onClick={handleOpenEditBandModal}
            className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium rounded-lg border border-slate-700 transition"
          >
            Editar Banda
          </button>
          <button
            onClick={() => setIsDeleteBandModalOpen(true)}
            className="px-3 py-1.5 bg-red-950/40 hover:bg-red-900/60 text-red-400 border border-red-800/60 text-xs font-medium rounded-lg transition"
          >
            Apagar Banda
          </button>
        </div>
      </div>

      {/* SETLISTS */}
      <section className="space-y-4">
        <div className="flex justify-between items-center">
          <div>
            <h3 className="text-xl font-bold text-slate-100">
              Alinhamentos / Setlists
            </h3>
            <p className="text-xs text-slate-400">
              Concertos e alinhamentos específicos criados para esta banda
            </p>
          </div>
          <button
            onClick={() => setIsSetlistModalOpen(true)}
            className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-medium rounded-lg transition"
          >
            + Nova Setlist
          </button>
        </div>

        {setlists.length === 0 ? (
          <div className="p-6 border border-dashed border-slate-800 rounded-xl text-center">
            <p className="text-xs text-slate-500">
              Ainda não criaste nenhuma setlist para esta banda.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {setlists.map((s) => (
              <div
                key={s.id}
                onClick={() => navigate(`/setlists/${s.id}`)}
                className="relative bg-slate-900 border border-slate-800 hover:border-indigo-500/40 p-5 rounded-xl cursor-pointer transition group flex flex-col justify-between"
              >
                <div>
                  <div className="flex justify-between items-start">
                    <h4 className="font-semibold text-slate-200 group-hover:text-indigo-300 transition pr-2">
                      {s.name}
                    </h4>

                    <div className="relative">
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          setOpenDropdownId(
                            openDropdownId === s.id ? null : s.id,
                          );
                        }}
                        className="p-1.5 text-slate-400 hover:text-slate-200 hover:bg-slate-800 rounded-lg transition"
                        title="Opções da Setlist"
                      >
                        <svg
                          className="w-4 h-4 fill-current"
                          viewBox="0 0 24 24"
                        >
                          <path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z" />
                        </svg>
                      </button>

                      {openDropdownId === s.id && (
                        <div
                          onClick={(e) => e.stopPropagation()}
                          className="absolute right-0 mt-1 w-36 bg-slate-800 border border-slate-700 rounded-xl shadow-xl z-20 py-1 text-xs"
                        >
                          <button
                            type="button"
                            onClick={(e) => handleOpenEditSetlist(s, e)}
                            className="w-full text-left px-3 py-2 text-slate-200 hover:bg-slate-700/80 transition"
                          >
                            Editar
                          </button>
                          <button
                            type="button"
                            onClick={(e) => handleOpenDeleteSetlist(s, e)}
                            className="w-full text-left px-3 py-2 text-red-400 hover:bg-slate-700/80 transition"
                          >
                            Apagar
                          </button>
                        </div>
                      )}
                    </div>
                  </div>

                  <p className="text-xs text-slate-400 mt-1 truncate">
                    {s.description || "Sem notas"}
                  </p>
                </div>

                <div className="flex items-center space-x-3 mt-4 text-xs font-mono text-slate-500">
                  <span>{s.totalSongs || 0} música(s)</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* POOL DE REPERTÓRIO */}
      <section className="space-y-4">
        <div className="flex justify-between items-center border-t border-slate-800 pt-8">
          <div>
            <h3 className="text-xl font-bold text-slate-100">
              Pool de Músicas (Repertório)
            </h3>
            <p className="text-xs text-slate-400">
              Músicas do catálogo global associadas a esta banda
            </p>
          </div>
          <button
            onClick={() => {
              setIsCreatingNewSong(false);
              setIsRepertoireModalOpen(true);
            }}
            className="px-3 py-1.5 bg-indigo-600/20 hover:bg-indigo-600/30 text-indigo-400 border border-indigo-500/30 text-xs font-medium rounded-lg transition"
          >
            + Adicionar ao Repertório
          </button>
        </div>

        {repertoire.length === 0 ? (
          <div className="p-6 border border-dashed border-slate-800 rounded-xl text-center">
            <p className="text-xs text-slate-500">
              Esta banda ainda não tem músicas no repertório.
            </p>
          </div>
        ) : (
          <div className="bg-slate-900 border border-slate-800 rounded-xl overflow-hidden">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="bg-slate-800/40 text-slate-400 text-xs uppercase border-b border-slate-800">
                  <th className="py-3 px-4">Música</th>
                  <th className="py-3 px-4">Artista</th>
                  <th className="py-3 px-4">Tom</th>
                  <th className="py-3 px-4">BPM</th>
                  <th className="py-3 px-4">Notas</th>
                  <th className="py-3 px-4 text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {repertoire.map((item) => (
                  <tr
                    key={item.id}
                    className="hover:bg-slate-800/30 transition text-slate-200"
                  >
                    <td className="py-3 px-4 font-semibold text-slate-100">
                      {item.title || item.song?.title || "Sem título"}
                    </td>
                    <td className="py-3 px-4 text-slate-400">
                      {item.artist || item.song?.artist || "Desconhecido"}
                    </td>
                    <td className="py-3 px-4">
                      <span className="px-2 py-0.5 bg-slate-800 border border-slate-700 rounded text-xs font-mono text-indigo-300">
                        {item.songKey || "-"}
                      </span>
                    </td>
                    <td className="py-3 px-4 font-mono text-xs">
                      {item.bpm || "-"}
                    </td>
                    <td className="py-3 px-4 text-xs text-slate-400 italic">
                      {item.notes || "-"}
                    </td>
                    <td className="py-3 px-4 text-right space-x-3">
                      <button
                        onClick={() => handleOpenEditRepertoire(item)}
                        className="text-xs text-indigo-400 hover:text-indigo-300 transition"
                      >
                        Editar
                      </button>
                      <button
                        onClick={() => setDeletingRepertoireItem(item)}
                        className="text-xs text-red-400 hover:text-red-300 transition"
                      >
                        Remover
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {/* SUGESTÕES */}
      <section className="space-y-4">
        <div className="flex justify-between items-center border-t border-slate-800 pt-8">
          <div>
            <h3 className="text-xl font-bold text-slate-100">Sugestões</h3>
            <p className="text-xs text-slate-400">
              Músicas a considerar para esta banda. Promove para o pool quando
              forem aprovadas
            </p>
          </div>
          <button
            onClick={handleOpenSuggestionModal}
            className="px-3 py-1.5 bg-indigo-600/20 hover:bg-indigo-600/30 text-indigo-400 border border-indigo-500/30 text-xs font-medium rounded-lg transition"
          >
            + Adicionar Sugestão
          </button>
        </div>

        {suggestions.length === 0 ? (
          <div className="p-6 border border-dashed border-slate-800 rounded-xl text-center">
            <p className="text-xs text-slate-500">
              Esta banda ainda não tem sugestões de músicas.
            </p>
          </div>
        ) : (
          <div className="bg-slate-900 border border-slate-800 rounded-xl overflow-hidden">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="bg-slate-800/40 text-slate-400 text-xs uppercase border-b border-slate-800">
                  <th className="py-3 px-4">Música</th>
                  <th className="py-3 px-4">Artista</th>
                  <th className="py-3 px-4 text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {suggestions.map((song) => (
                  <tr
                    key={song.id}
                    className="hover:bg-slate-800/30 transition text-slate-200"
                  >
                    <td className="py-3 px-4 font-semibold text-slate-100">
                      {song.title}
                    </td>
                    <td className="py-3 px-4 text-slate-400">
                      {song.artist || "Desconhecido"}
                    </td>
                    <td className="py-3 px-4 text-right space-x-3">
                      <button
                        onClick={() => handlePromoteSuggestion(song.id)}
                        className="text-xs text-emerald-400 hover:text-emerald-300 transition"
                      >
                        Promover ao Pool
                      </button>
                      <button
                        onClick={() => handleOpenEditSuggestion(song)}
                        className="text-xs text-indigo-400 hover:text-indigo-300 transition"
                      >
                        Editar
                      </button>
                      <button
                        onClick={() => handleRemoveSuggestion(song.id)}
                        className="text-xs text-red-400 hover:text-red-300 transition"
                      >
                        Retirar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {/* Modal Adicionar Sugestão */}
      {isSuggestionModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-bold text-slate-100">
                Adicionar Sugestão
              </h3>
              <button
                type="button"
                onClick={() =>
                  setIsCreatingNewSuggestion(!isCreatingNewSuggestion)
                }
                className="text-xs text-indigo-400 hover:underline"
              >
                {isCreatingNewSuggestion
                  ? "← Escolher Existente"
                  : "+ Criar Nova Música"}
              </button>
            </div>

            <form onSubmit={handleAddSuggestion} className="space-y-4">
              {isCreatingNewSuggestion ? (
                <>
                  <p className="text-xs text-slate-400">
                    A música será também adicionada ao catálogo geral.
                  </p>
                  <div>
                    <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                      Título da Nova Música
                    </label>
                    <input
                      type="text"
                      required
                      value={newSuggestionTitle}
                      onChange={(e) => setNewSuggestionTitle(e.target.value)}
                      placeholder="Ex: Superstition"
                      className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                      Artista / Banda
                    </label>
                    <input
                      type="text"
                      value={newSuggestionArtist}
                      onChange={(e) => setNewSuggestionArtist(e.target.value)}
                      placeholder="Ex: Stevie Wonder"
                      className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                    />
                  </div>
                </>
              ) : (
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                    Música do Catálogo
                  </label>
                  <select
                    required
                    value={selectedSuggestionSongId}
                    onChange={(e) =>
                      setSelectedSuggestionSongId(
                        e.target.value ? Number(e.target.value) : "",
                      )
                    }
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                  >
                    <option value="">Seleciona uma música...</option>
                    {songsAvailableForSuggestion.map((s) => (
                      <option key={s.id} value={s.id}>
                        {s.title} - {s.artist}
                      </option>
                    ))}
                  </select>
                  {songsAvailableForSuggestion.length === 0 && (
                    <p className="text-xs text-slate-500 mt-2">
                      Todas as músicas do catálogo já estão no pool ou nas
                      sugestões desta banda.
                    </p>
                  )}
                </div>
              )}

              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setIsSuggestionModalOpen(false)}
                  className="px-4 py-2 text-slate-400 hover:text-slate-200 text-sm font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium rounded-lg transition"
                >
                  {isCreatingNewSuggestion ? "Criar & Sugerir" : "Adicionar"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Editar Sugestão */}
      {editingSuggestion && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              Editar Sugestão
            </h3>
            <form onSubmit={handleUpdateSuggestion} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Título da Música (Global)
                </label>
                <input
                  type="text"
                  required
                  value={editSuggestionTitle}
                  onChange={(e) => setEditSuggestionTitle(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Artista / Banda (Global)
                </label>
                <input
                  type="text"
                  value={editSuggestionArtist}
                  onChange={(e) => setEditSuggestionArtist(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <p className="text-xs text-slate-500">
                Estes dados são do catálogo geral: a alteração aplica-se em
                todas as bandas onde a música aparece.
              </p>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setEditingSuggestion(null)}
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

      {/* Modal Editar Música do Repertório */}
      {editingRepertoireItem && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              Editar Música no Repertório
            </h3>
            <form onSubmit={handleUpdateRepertoireItem} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Título da Música (Global)
                </label>
                <input
                  type="text"
                  required
                  value={editSongTitle}
                  onChange={(e) => setEditSongTitle(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Artista / Banda (Global)
                </label>
                <input
                  type="text"
                  value={editSongArtist}
                  onChange={(e) => setEditSongArtist(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                    Tom da Banda
                  </label>
                  <input
                    type="text"
                    value={editSongKey}
                    onChange={(e) => setEditSongKey(e.target.value)}
                    placeholder="Ex: Abm"
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                    BPM
                  </label>
                  <input
                    type="number"
                    value={editSongBpm}
                    onChange={(e) =>
                      setEditSongBpm(
                        e.target.value ? Number(e.target.value) : "",
                      )
                    }
                    placeholder="Ex: 110"
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Notas / Arranjo da Banda
                </label>
                <input
                  type="text"
                  value={editSongNotes}
                  onChange={(e) => setEditSongNotes(e.target.value)}
                  placeholder="Ex: Tom subido para a vocalista"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>

              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setEditingRepertoireItem(null)}
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

      {/* Modal Confirmar Eliminação de Música do Repertório */}
      {deletingRepertoireItem && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md space-y-4">
            <h3 className="text-xl font-bold text-slate-100">
              Remover Música do Repertório
            </h3>
            <p className="text-sm text-slate-300">
              Tens a certeza que desejas remover{" "}
              <span className="font-semibold text-slate-100">
                "
                {deletingRepertoireItem.title ||
                  deletingRepertoireItem.song?.title}
                "
              </span>{" "}
              do repertório desta banda?
            </p>
            <div className="p-3 bg-amber-950/40 border border-amber-800/60 rounded-xl">
              <p className="text-xs text-amber-300 font-medium">
                ⚠️ Aviso de Alinhamentos
              </p>
              <p className="text-xs text-amber-200/80 mt-1">
                Se esta música estiver incluída em alguma setlist/alinhamento da
                banda, será também removida das respetivas setlists.
              </p>
            </div>
            <div className="flex justify-end space-x-3 pt-2">
              <button
                type="button"
                onClick={() => setDeletingRepertoireItem(null)}
                className="px-4 py-2 text-slate-400 hover:text-slate-200 text-sm font-medium"
              >
                Cancelar
              </button>
              <button
                type="button"
                onClick={handleConfirmDeleteRepertoireItem}
                className="px-4 py-2 bg-red-600 hover:bg-red-500 text-white text-sm font-medium rounded-lg transition"
              >
                Sim, Remover
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal Editar Banda */}
      {isEditBandModalOpen && (
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
                  value={editBandName}
                  onChange={(e) => setEditBandName(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Descrição / Género
                </label>
                <input
                  type="text"
                  value={editBandDesc}
                  onChange={(e) => setEditBandDesc(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setIsEditBandModalOpen(false)}
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
      {isDeleteBandModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md space-y-4">
            <h3 className="text-xl font-bold text-slate-100">Apagar Banda</h3>
            <p className="text-sm text-slate-300">
              Tens a certeza que desejas apagar a banda{" "}
              <span className="font-semibold text-slate-100">
                "{band.name}"
              </span>
              ?
            </p>
            <p className="text-xs text-slate-500">
              Esta ação elimina a banda e os seus alinhamentos associados. As
              músicas no catálogo geral não serão eliminadas.
            </p>
            <div className="flex justify-end space-x-3 pt-2">
              <button
                type="button"
                onClick={() => setIsDeleteBandModalOpen(false)}
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

      {/* Modal Adicionar ao Repertório */}
      {isRepertoireModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-bold text-slate-100">
                Adicionar ao Repertório
              </h3>
              <button
                type="button"
                onClick={() => setIsCreatingNewSong(!isCreatingNewSong)}
                className="text-xs text-indigo-400 hover:underline"
              >
                {isCreatingNewSong
                  ? "← Escolher Existente"
                  : "+ Criar Nova Música"}
              </button>
            </div>

            <form onSubmit={handleAddSongToRepertoire} className="space-y-4">
              {isCreatingNewSong ? (
                <>
                  <div>
                    <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                      Título da Nova Música
                    </label>
                    <input
                      type="text"
                      required
                      value={newTitle}
                      onChange={(e) => setNewTitle(e.target.value)}
                      placeholder="Ex: Superstition"
                      className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                      Artista / Banda
                    </label>
                    <input
                      type="text"
                      value={newArtist}
                      onChange={(e) => setNewArtist(e.target.value)}
                      placeholder="Ex: Stevie Wonder"
                      className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                    />
                  </div>
                </>
              ) : (
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                    Música do Catálogo
                  </label>
                  <select
                    required
                    value={selectedSongId}
                    onChange={(e) =>
                      setSelectedSongId(
                        e.target.value ? Number(e.target.value) : "",
                      )
                    }
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                  >
                    <option value="">Seleciona uma música...</option>
                    {globalSongs.map((s) => (
                      <option key={s.id} value={s.id}>
                        {s.title} - {s.artist}
                      </option>
                    ))}
                  </select>
                </div>
              )}

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                    Tom da Banda
                  </label>
                  <input
                    type="text"
                    value={songKey}
                    onChange={(e) => setSongKey(e.target.value)}
                    placeholder="Ex: Abm"
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                    BPM
                  </label>
                  <input
                    type="number"
                    value={bpm}
                    onChange={(e) =>
                      setBpm(e.target.value ? Number(e.target.value) : "")
                    }
                    placeholder="Ex: 110"
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Notas / Arranjo
                </label>
                <input
                  type="text"
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                  placeholder="Ex: Tom subido para a vocalista"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>

              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setIsRepertoireModalOpen(false)}
                  className="px-4 py-2 text-slate-400 hover:text-slate-200 text-sm font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium rounded-lg transition"
                >
                  {isCreatingNewSong ? "Criar & Adicionar" : "Adicionar"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Criar Setlist */}
      {isSetlistModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              Nova Setlist para {band.name}
            </h3>
            <form onSubmit={handleCreateSetlist} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Nome do Concerto / Evento
                </label>
                <input
                  type="text"
                  required
                  value={setlistName}
                  onChange={(e) => setSetlistName(e.target.value)}
                  placeholder="Ex: Concerto de Verão"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Descrição
                </label>
                <input
                  type="text"
                  value={setlistDesc}
                  onChange={(e) => setSetlistDesc(e.target.value)}
                  placeholder="Ex: Setlist de 90 min"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setIsSetlistModalOpen(false)}
                  className="px-4 py-2 text-slate-400 hover:text-slate-200 text-sm font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium rounded-lg transition"
                >
                  Criar & Editar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Editar Setlist (via Cartão) */}
      {editingSetlist && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              Editar Setlist
            </h3>
            <form onSubmit={handleUpdateSetlist} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Nome da Setlist
                </label>
                <input
                  type="text"
                  required
                  value={editSetlistName}
                  onChange={(e) => setEditSetlistName(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Descrição / Notas
                </label>
                <input
                  type="text"
                  value={editSetlistDesc}
                  onChange={(e) => setEditSetlistDesc(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setEditingSetlist(null)}
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

      {/* Modal Apagar Setlist (via Cartão) */}
      {deletingSetlist && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md space-y-4">
            <h3 className="text-xl font-bold text-slate-100">Apagar Setlist</h3>
            <p className="text-sm text-slate-300">
              Tens a certeza que desejas apagar a setlist{" "}
              <span className="font-semibold text-slate-100">
                "{deletingSetlist.name}"
              </span>
              ?
            </p>
            <p className="text-xs text-slate-500">
              Esta ação elimina o alinhamento. As músicas continuarão no
              repertório da banda.
            </p>
            <div className="flex justify-end space-x-3 pt-2">
              <button
                type="button"
                onClick={() => setDeletingSetlist(null)}
                className="px-4 py-2 text-slate-400 hover:text-slate-200 text-sm font-medium"
              >
                Cancelar
              </button>
              <button
                type="button"
                onClick={handleDeleteSetlist}
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

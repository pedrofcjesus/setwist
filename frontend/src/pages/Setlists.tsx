import { useEffect, useState, type FormEvent } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/axios";

interface SongItem {
  id: number;
  position: number;
  notes: string;
  repertoireItem: {
    id: number;
    songKey: string;
    bpm: number;
    title?: string;
    artist?: string;
    song?: {
      id?: number;
      title: string;
      artist: string;
    };
  };
}

interface SetlistDetailData {
  id: number;
  name: string;
  description: string;
  band?: {
    id: number;
    name: string;
  };
  setlistSongs: SongItem[];
}

interface RepertoireItem {
  id: number;
  songId?: number;
  title?: string;
  artist?: string;
  songKey: string;
  bpm: number;
  notes?: string;
  song?: {
    id?: number;
    title: string;
    artist: string;
  };
}

export function Setlists() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [setlist, setSetlist] = useState<SetlistDetailData | null>(null);
  const [repertoire, setRepertoire] = useState<RepertoireItem[]>([]);
  const [loading, setLoading] = useState(true);

  // Modal para criar nova música na hora
  const [isNewSongModalOpen, setIsNewSongModalOpen] = useState(false);
  const [newTitle, setNewTitle] = useState("");
  const [newArtist, setNewArtist] = useState("");
  const [songKey, setSongKey] = useState("");
  const [bpm, setBpm] = useState<number | "">("");

  // Modal para Editar Música do Repertório
  const [editingRepItem, setEditingRepItem] = useState<RepertoireItem | null>(
    null,
  );
  const [editRepTitle, setEditRepTitle] = useState("");
  const [editRepArtist, setEditRepArtist] = useState("");
  const [editRepKey, setEditRepKey] = useState("");
  const [editRepBpm, setEditRepBpm] = useState<number | "">("");

  // Modal para Remover Música do Repertório
  const [deletingRepItem, setDeletingRepItem] = useState<RepertoireItem | null>(
    null,
  );

  // Modal para Editar Setlist
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editName, setEditName] = useState("");
  const [editDescription, setEditDescription] = useState("");

  // Modal para Apagar Setlist
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  const fetchSetlistAndRepertoire = async () => {
    try {
      const setlistRes = await api.get(`/setlists/${id}`);
      setSetlist(setlistRes.data);

      if (setlistRes.data.band?.id) {
        const repRes = await api.get(
          `/bands/${setlistRes.data.band.id}/repertoire`,
        );
        setRepertoire(repRes.data);
      }
    } catch (err) {
      console.error("Erro ao carregar setlist:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSetlistAndRepertoire();
  }, [id]);

  // Editar Setlist
  const handleOpenEditModal = () => {
    if (!setlist) return;
    setEditName(setlist.name);
    setEditDescription(setlist.description || "");
    setIsEditModalOpen(true);
  };

  const handleUpdateSetlist = async (e: FormEvent) => {
    e.preventDefault();
    if (!setlist || !editName) return;

    try {
      await api.put(`/setlists/${id}`, {
        name: editName,
        description: editDescription,
        bandId: setlist.band?.id || null,
      });
      setIsEditModalOpen(false);
      fetchSetlistAndRepertoire();
    } catch (err) {
      console.error("Erro ao atualizar setlist:", err);
    }
  };

  // Apagar Setlist
  const handleDeleteSetlist = async () => {
    if (!setlist) return;

    try {
      await api.delete(`/setlists/${id}`);
      setIsDeleteModalOpen(false);
      navigate(setlist.band ? `/bandas/${setlist.band.id}` : "/bandas");
    } catch (err) {
      console.error("Erro ao apagar setlist:", err);
    }
  };

  // Abrir Modal de Edição da Música no Repertório
  const handleOpenEditRepItem = (rep: RepertoireItem) => {
    setEditingRepItem(rep);
    setEditRepTitle(rep.title || rep.song?.title || "");
    setEditRepArtist(rep.artist || rep.song?.artist || "");
    setEditRepKey(rep.songKey || "");
    setEditRepBpm(rep.bpm || "");
  };

  // Guardar Edição do Repertório (Global + Banda)
  const handleUpdateRepItem = async (e: FormEvent) => {
    e.preventDefault();
    if (!editingRepItem || !setlist?.band?.id) return;

    try {
      const targetSongId = editingRepItem.songId ?? editingRepItem.song?.id;

      // 1. Atualizar dados globais da música
      if (targetSongId) {
        await api.put(`/songs/${targetSongId}`, {
          title: editRepTitle,
          artist: editRepArtist || "Desconhecido",
        });
      }

      // 2. Atualizar tom e bpm no repertório da banda
      await api.put(
        `/bands/${setlist.band.id}/repertoire/${editingRepItem.id}`,
        {
          songKey: editRepKey,
          bpm: editRepBpm ? Number(editRepBpm) : null,
        },
      );

      setEditingRepItem(null);
      fetchSetlistAndRepertoire();
    } catch (err: any) {
      console.error("Erro ao atualizar música do repertório:", err);
      alert(
        "Não foi possível guardar as alterações: " +
          (err.response?.data?.message || err.message || "Erro de servidor."),
      );
    }
  };

  // Remover / Apagar Música do Repertório da Banda
  const handleConfirmDeleteRepItem = async () => {
    if (!deletingRepItem || !setlist?.band?.id) return;

    try {
      await api.delete(
        `/bands/${setlist.band.id}/repertoire/${deletingRepItem.id}`,
      );
      setDeletingRepItem(null);
      fetchSetlistAndRepertoire();
    } catch (err: any) {
      console.error("Erro ao remover do repertório:", err);
      alert(
        "Não foi possível remover a música: " +
          (err.response?.data?.message || err.message || "Erro de servidor."),
      );
    }
  };

  const handleAddSongToSetlist = async (repertoireItemId: number) => {
    try {
      await api.post(`/setlists/${id}/songs`, { repertoireItemId });
      fetchSetlistAndRepertoire();
    } catch (err) {
      console.error("Erro ao adicionar música à setlist:", err);
    }
  };

  const handleCreateAndAddSong = async (e: FormEvent) => {
    e.preventDefault();
    if (!newTitle || !setlist?.band?.id) return;

    try {
      const songRes = await api.post("/songs", {
        title: newTitle,
        artist: newArtist || "Desconhecido",
      });

      const repRes = await api.post(`/bands/${setlist.band.id}/repertoire`, {
        songId: songRes.data.id,
        songKey,
        bpm: bpm ? Number(bpm) : null,
      });

      await api.post(`/setlists/${id}/songs`, {
        repertoireItemId: repRes.data.id,
      });

      setNewTitle("");
      setNewArtist("");
      setSongKey("");
      setBpm("");
      setIsNewSongModalOpen(false);

      fetchSetlistAndRepertoire();
    } catch (err) {
      console.error("Erro ao criar e adicionar nova música:", err);
    }
  };

  const handleRemoveSong = async (repertoireItemId: number) => {
    try {
      await api.delete(`/setlists/${id}/songs/${repertoireItemId}`);
      fetchSetlistAndRepertoire();
    } catch (err) {
      console.error("Erro ao remover música da setlist:", err);
    }
  };

  const handleMove = async (index: number, direction: "up" | "down") => {
    if (!setlist) return;
    const items = [...setlist.setlistSongs];
    const targetIndex = direction === "up" ? index - 1 : index + 1;

    if (targetIndex < 0 || targetIndex >= items.length) return;

    const temp = items[index];
    items[index] = items[targetIndex];
    items[targetIndex] = temp;

    const repertoireItemIds = items.map((item) => item.repertoireItem.id);

    try {
      await api.put(`/setlists/${id}/songs/reorder`, { repertoireItemIds });
      fetchSetlistAndRepertoire();
    } catch (err) {
      console.error("Erro ao reordenar setlist:", err);
    }
  };

  if (loading)
    return (
      <p className="text-slate-400 text-center py-12">A carregar setlist...</p>
    );
  if (!setlist)
    return (
      <p className="text-slate-400 text-center py-12">
        Setlist não encontrada.
      </p>
    );

  const activeRepertoireIds = setlist.setlistSongs.map(
    (s) => s.repertoireItem.id,
  );

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <button
            onClick={() =>
              navigate(setlist.band ? `/bandas/${setlist.band.id}` : "/bandas")
            }
            className="text-xs text-indigo-400 hover:underline mb-2 inline-block"
          >
            ← Voltar para {setlist.band ? setlist.band.name : "Bandas"}
          </button>
          <h2 className="text-3xl font-bold text-slate-100">{setlist.name}</h2>
          <p className="text-slate-400 text-sm mt-1">
            {setlist.description || "Alinhamento do concerto"}
          </p>
        </div>

        {/* Botões Editar / Apagar Setlist */}
        <div className="flex space-x-2 pt-1">
          <button
            onClick={handleOpenEditModal}
            className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium rounded-lg border border-slate-700 transition"
          >
            Editar Setlist
          </button>
          <button
            onClick={() => setIsDeleteModalOpen(true)}
            className="px-3 py-1.5 bg-red-950/40 hover:bg-red-900/60 text-red-400 border border-red-800/60 text-xs font-medium rounded-lg transition"
          >
            Apagar
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* COLUNA ESQUERDA: ALINHAMENTO */}
        <div className="lg:col-span-2 space-y-4">
          <div className="flex justify-between items-center bg-slate-900 p-4 rounded-xl border border-slate-800">
            <h3 className="font-semibold text-slate-200">
              Alinhamento ({setlist.setlistSongs.length} músicas)
            </h3>
            <span className="text-xs text-slate-500 font-mono">
              Usa as setas para reordenar
            </span>
          </div>

          {setlist.setlistSongs.length === 0 ? (
            <div className="p-8 border border-dashed border-slate-800 rounded-2xl text-center">
              <p className="text-slate-500 text-sm">
                A setlist está vazia. Adiciona músicas a partir do repertório à
                direita!
              </p>
            </div>
          ) : (
            <div className="space-y-2">
              {setlist.setlistSongs.map((item, index) => {
                const songTitle =
                  item.repertoireItem.title ||
                  item.repertoireItem.song?.title ||
                  "Sem título";
                const songArtist =
                  item.repertoireItem.artist ||
                  item.repertoireItem.song?.artist ||
                  "Desconhecido";

                return (
                  <div
                    key={item.id}
                    className="flex items-center justify-between p-4 bg-slate-900 border border-slate-800 rounded-xl hover:border-slate-700 transition"
                  >
                    <div className="flex items-center space-x-4">
                      <div className="flex flex-col space-y-1">
                        <button
                          disabled={index === 0}
                          onClick={() => handleMove(index, "up")}
                          className="text-xs text-slate-500 hover:text-indigo-400 disabled:opacity-20 transition"
                        >
                          ▲
                        </button>
                        <button
                          disabled={index === setlist.setlistSongs.length - 1}
                          onClick={() => handleMove(index, "down")}
                          className="text-xs text-slate-500 hover:text-indigo-400 disabled:opacity-20 transition"
                        >
                          ▼
                        </button>
                      </div>

                      <span className="font-mono text-sm font-bold text-slate-500 w-6">
                        {index + 1}.
                      </span>

                      <div>
                        <h4 className="font-bold text-slate-100 text-sm">
                          {songTitle}
                        </h4>
                        <p className="text-xs text-slate-400">{songArtist}</p>
                      </div>
                    </div>

                    <div className="flex items-center space-x-4">
                      <span className="px-2 py-0.5 bg-slate-800 border border-slate-700 rounded text-xs font-mono text-indigo-300">
                        {item.repertoireItem.songKey || "-"}
                      </span>
                      <span className="text-xs font-mono text-slate-400">
                        {item.repertoireItem.bpm
                          ? `${item.repertoireItem.bpm} BPM`
                          : "-"}
                      </span>
                      <button
                        onClick={() => handleRemoveSong(item.repertoireItem.id)}
                        className="p-1 text-slate-500 hover:text-red-400 text-xs transition"
                        title="Remover da setlist"
                      >
                        ✕
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* COLUNA DIREITA: POOL DE REPERTÓRIO DA BANDA */}
        <div className="space-y-4">
          <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 flex justify-between items-center">
            <div>
              <h3 className="font-semibold text-slate-200">Repertório</h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Clica no + para adicionar ao alinhamento
              </p>
            </div>
            <button
              onClick={() => setIsNewSongModalOpen(true)}
              className="text-xs text-indigo-400 hover:underline font-medium"
            >
              + Criar Nova
            </button>
          </div>

          <div className="space-y-2 max-h-[600px] overflow-y-auto pr-1">
            {repertoire.length === 0 ? (
              <p className="text-xs text-slate-500 text-center py-4">
                Sem músicas no repertório desta banda.
              </p>
            ) : (
              repertoire.map((rep) => {
                const isAdded = activeRepertoireIds.includes(rep.id);
                const title = rep.title || rep.song?.title || "Sem título";
                const artist = rep.artist || rep.song?.artist || "Desconhecido";

                return (
                  <div
                    key={rep.id}
                    className={`p-3 rounded-xl border flex items-center justify-between text-xs transition ${
                      isAdded
                        ? "bg-slate-900/40 border-slate-800/60 opacity-60"
                        : "bg-slate-900 border-slate-800 hover:border-slate-700"
                    }`}
                  >
                    <div className="pr-2">
                      <div className="font-semibold text-slate-200">
                        {title}
                      </div>
                      <div className="text-slate-400">{artist}</div>
                      <div className="text-[10px] text-slate-500 font-mono mt-0.5">
                        {rep.songKey || "-"} |{" "}
                        {rep.bpm ? `${rep.bpm} BPM` : "-"}
                      </div>
                    </div>

                    <div className="flex items-center space-x-1.5">
                      <button
                        onClick={() => handleOpenEditRepItem(rep)}
                        className="p-1.5 text-slate-400 hover:text-indigo-400 transition"
                        title="Editar música"
                      >
                        ✎
                      </button>
                      <button
                        onClick={() => setDeletingRepItem(rep)}
                        className="p-1.5 text-slate-400 hover:text-red-400 transition"
                        title="Apagar do repertório"
                      >
                        🗑
                      </button>
                      <button
                        disabled={isAdded}
                        onClick={() => handleAddSongToSetlist(rep.id)}
                        className={`px-2.5 py-1 rounded-lg font-medium transition ${
                          isAdded
                            ? "bg-slate-800 text-slate-600 cursor-not-allowed"
                            : "bg-indigo-600 hover:bg-indigo-500 text-white"
                        }`}
                      >
                        {isAdded ? "Adicionada" : "+"}
                      </button>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </div>

      {/* Modal Editar Música do Repertório */}
      {editingRepItem && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              Editar Música
            </h3>
            <form onSubmit={handleUpdateRepItem} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Título da Música (Global)
                </label>
                <input
                  type="text"
                  required
                  value={editRepTitle}
                  onChange={(e) => setEditRepTitle(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Artista / Banda (Global)
                </label>
                <input
                  type="text"
                  value={editRepArtist}
                  onChange={(e) => setEditRepArtist(e.target.value)}
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
                    value={editRepKey}
                    onChange={(e) => setEditRepKey(e.target.value)}
                    placeholder="Ex: C"
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                    BPM
                  </label>
                  <input
                    type="number"
                    value={editRepBpm}
                    onChange={(e) =>
                      setEditRepBpm(
                        e.target.value ? Number(e.target.value) : "",
                      )
                    }
                    placeholder="Ex: 120"
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                  />
                </div>
              </div>

              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setEditingRepItem(null)}
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

      {/* Modal Confirmar Eliminação do Repertório */}
      {deletingRepItem && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md space-y-4">
            <h3 className="text-xl font-bold text-slate-100">
              Remover do Repertório
            </h3>
            <p className="text-sm text-slate-300">
              Tens a certeza que desejas remover{" "}
              <span className="font-semibold text-slate-100">
                "{deletingRepItem.title || deletingRepItem.song?.title}"
              </span>{" "}
              do repertório desta banda?
            </p>
            <div className="p-3 bg-amber-950/40 border border-amber-800/60 rounded-xl">
              <p className="text-xs text-amber-300 font-medium">
                ⚠️ Aviso de Alinhamentos
              </p>
              <p className="text-xs text-amber-200/80 mt-1">
                Se esta música estiver presente em algum alinhamento/setlist,
                será automaticamente removida dessas setlists.
              </p>
            </div>
            <div className="flex justify-end space-x-3 pt-2">
              <button
                type="button"
                onClick={() => setDeletingRepItem(null)}
                className="px-4 py-2 text-slate-400 hover:text-slate-200 text-sm font-medium"
              >
                Cancelar
              </button>
              <button
                type="button"
                onClick={handleConfirmDeleteRepItem}
                className="px-4 py-2 bg-red-600 hover:bg-red-500 text-white text-sm font-medium rounded-lg transition"
              >
                Sim, Remover
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal Editar Setlist */}
      {isEditModalOpen && (
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
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Descrição / Notas
                </label>
                <textarea
                  rows={3}
                  value={editDescription}
                  onChange={(e) => setEditDescription(e.target.value)}
                  placeholder="Ex: Concerto de Verão na Praça Central"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm resize-none"
                />
              </div>

              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setIsEditModalOpen(false)}
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

      {/* Modal Confirmar Eliminação */}
      {isDeleteModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md space-y-4">
            <h3 className="text-xl font-bold text-slate-100">Apagar Setlist</h3>
            <p className="text-sm text-slate-300">
              Tens a certeza que desejas apagar a setlist{" "}
              <span className="font-semibold text-slate-100">
                "{setlist.name}"
              </span>
              ?
            </p>
            <p className="text-xs text-slate-500">
              Esta ação elimina o alinhamento de concerto. As músicas
              continuarão guardadas no repertório da banda e no teu catálogo.
            </p>
            <div className="flex justify-end space-x-3 pt-2">
              <button
                type="button"
                onClick={() => setIsDeleteModalOpen(false)}
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

      {/* Modal Criar Nova Música a partir da Setlist */}
      {isNewSongModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              Criar Nova Música
            </h3>
            <p className="text-xs text-slate-400 mb-4">
              A música será guardada no catálogo geral, associada ao repertório
              de {setlist.band?.name} e adicionada a este alinhamento.
            </p>
            <form onSubmit={handleCreateAndAddSong} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Título da Música
                </label>
                <input
                  type="text"
                  required
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  placeholder="Ex: Fly Me to the Moon"
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
                  placeholder="Ex: Frank Sinatra"
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
                    value={songKey}
                    onChange={(e) => setSongKey(e.target.value)}
                    placeholder="Ex: C"
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
                    placeholder="Ex: 120"
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500 text-sm"
                  />
                </div>
              </div>

              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setIsNewSongModalOpen(false)}
                  className="px-4 py-2 text-slate-400 hover:text-slate-200 text-sm font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium rounded-lg transition"
                >
                  Criar & Adicionar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

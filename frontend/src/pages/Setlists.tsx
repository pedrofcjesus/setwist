import { useEffect, useState } from "react";
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
  title?: string;
  artist?: string;
  songKey: string;
  bpm: number;
  song?: {
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

  const handleAddSongToSetlist = async (repertoireItemId: number) => {
    try {
      await api.post(`/setlists/${id}/songs`, { repertoireItemId });
      fetchSetlistAndRepertoire();
    } catch (err) {
      console.error("Erro ao adicionar música à setlist:", err);
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
      {/* Voltar para a página da Banda */}
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

      {/* PAINEL DIVIDIDO EM 2 COLUNAS */}
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

        {/* COLUNA DIREITA: POOL DE REPERTÓRIO */}
        <div className="space-y-4">
          <div className="bg-slate-900 p-4 rounded-xl border border-slate-800">
            <h3 className="font-semibold text-slate-200">
              Repertório da Banda
            </h3>
            <p className="text-xs text-slate-500 mt-0.5">
              Clica no + para adicionar ao alinhamento
            </p>
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
                        ? "bg-slate-900/40 border-slate-800/60 opacity-50"
                        : "bg-slate-900 border-slate-800 hover:border-slate-700"
                    }`}
                  >
                    <div>
                      <div className="font-semibold text-slate-200">
                        {title}
                      </div>
                      <div className="text-slate-400">{artist}</div>
                    </div>

                    <button
                      disabled={isAdded}
                      onClick={() => handleAddSongToSetlist(rep.id)}
                      className={`px-3 py-1.5 rounded-lg font-medium transition ${
                        isAdded
                          ? "bg-slate-800 text-slate-600 cursor-not-allowed"
                          : "bg-indigo-600 hover:bg-indigo-500 text-white"
                      }`}
                    >
                      {isAdded ? "Adicionada" : "+ Adicionar"}
                    </button>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

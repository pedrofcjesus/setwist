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
  const [setlists, setSetlists] = useState<Setlist[]>([]);
  const [globalSongs, setGlobalSongs] = useState<Song[]>([]);
  const [loading, setLoading] = useState(true);

  // Modais
  const [isRepertoireModalOpen, setIsRepertoireModalOpen] = useState(false);
  const [isSetlistModalOpen, setIsSetlistModalOpen] = useState(false);

  // Alternar entre selecionar existente ou criar nova
  const [isCreatingNewSong, setIsCreatingNewSong] = useState(false);

  // Form Repertório / Nova Música
  const [selectedSongId, setSelectedSongId] = useState<number | "">("");
  const [newTitle, setNewTitle] = useState("");
  const [newArtist, setNewArtist] = useState("");
  const [songKey, setSongKey] = useState("");
  const [bpm, setBpm] = useState<number | "">("");
  const [notes, setNotes] = useState("");

  // Form Setlist
  const [setlistName, setSetlistName] = useState("");
  const [setlistDesc, setSetlistDesc] = useState("");

  const fetchData = async () => {
    try {
      const [bandRes, repRes, setlistRes, globalRes] = await Promise.all([
        api.get(`/bands/${id}`),
        api.get(`/bands/${id}/repertoire`),
        api.get(`/setlists`),
        api.get(`/songs`),
      ]);

      setBand(bandRes.data);
      setRepertoire(repRes.data);
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

  const handleAddSongToRepertoire = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      let targetSongId = selectedSongId;

      // Se for para criar uma nova música primeiro
      if (isCreatingNewSong) {
        if (!newTitle) return;
        const songRes = await api.post("/songs", {
          title: newTitle,
          artist: newArtist || "Desconhecido",
        });
        targetSongId = songRes.data.id;
      }

      if (!targetSongId) return;

      // Associa ao repertório da banda
      await api.post(`/bands/${id}/repertoire`, {
        songId: Number(targetSongId),
        songKey,
        bpm: bpm ? Number(bpm) : null,
        notes,
      });

      // Reset dos campos
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

  const handleDeleteRepertoireItem = async (repertoireId: number) => {
    if (!confirm("Tem a certeza que deseja remover esta música do repertório?"))
      return;
    try {
      await api.delete(`/bands/${id}/repertoire/${repertoireId}`);
      fetchData();
    } catch (err) {
      console.error("Erro ao remover do repertório:", err);
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
      navigate(`/setlists/${res.data.id}`);
    } catch (err) {
      console.error("Erro ao criar setlist:", err);
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

  return (
    <div className="space-y-10">
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
                className="bg-slate-900 border border-slate-800 hover:border-indigo-500/40 p-5 rounded-xl cursor-pointer transition group"
              >
                <h4 className="font-semibold text-slate-200 group-hover:text-indigo-300 transition">
                  {s.name}
                </h4>
                <p className="text-xs text-slate-400 mt-1 truncate">
                  {s.description || "Sem notas"}
                </p>
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
                    <td className="py-3 px-4 text-right">
                      <button
                        onClick={() => handleDeleteRepertoireItem(item.id)}
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
    </div>
  );
}

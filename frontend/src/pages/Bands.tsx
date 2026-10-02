import React, { useEffect, useState } from "react";
import api from "../api/axios";

interface Band {
  id: number;
  name: string;
  description: string;
}

interface Song {
  id: number;
  title: string;
  artist: string;
}

interface BandRepertoireItem {
  id: number;
  song: Song;
  songKey: string;
  bpm: number;
  notes: string;
}

export function Bands() {
  const [bands, setBands] = useState<Band[]>([]);
  const [selectedBand, setSelectedBand] = useState<Band | null>(null);
  const [repertoire, setRepertoire] = useState<BandRepertoireItem[]>([]);
  const [globalSongs, setGlobalSongs] = useState<Song[]>([]);
  const [loading, setLoading] = useState(true);

  // Modais
  const [isBandModalOpen, setIsBandModalOpen] = useState(false);
  const [isRepertoireModalOpen, setIsRepertoireModalOpen] = useState(false);

  // Form Banda
  const [bandName, setBandName] = useState("");
  const [bandDesc, setBandDesc] = useState("");

  // Form Repertório
  const [selectedSongId, setSelectedSongId] = useState<number | "">("");
  const [songKey, setSongKey] = useState("");
  const [bpm, setBpm] = useState<number | "">("");
  const [notes, setNotes] = useState("");

  const fetchBands = async () => {
    try {
      const response = await api.get("/bands");
      setBands(response.data);
      if (response.data.length > 0 && !selectedBand) {
        selectBand(response.data[0]);
      }
    } catch (err) {
      console.error("Erro ao carregar bandas:", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchGlobalSongs = async () => {
    try {
      const response = await api.get("/songs");
      setGlobalSongs(response.data);
    } catch (err) {
      console.error("Erro ao carregar catálogo de músicas:", err);
    }
  };

  const selectBand = async (band: Band) => {
    setSelectedBand(band);
    try {
      const response = await api.get(`/bands/${band.id}/repertoire`);
      setRepertoire(response.data);
    } catch (err) {
      console.error("Erro ao carregar repertório da banda:", err);
    }
  };

  useEffect(() => {
    fetchBands();
    fetchGlobalSongs();
  }, []);

  const handleCreateBand = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.post("/bands", {
        name: bandName,
        description: bandDesc,
      });
      setBandName("");
      setBandDesc("");
      setIsBandModalOpen(false);
      fetchBands();
      selectBand(response.data);
    } catch (err) {
      console.error("Erro ao criar banda:", err);
    }
  };

  const handleAddToRepertoire = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedBand || !selectedSongId) return;

    try {
      await api.post(`/bands/${selectedBand.id}/repertoire`, {
        songId: Number(selectedSongId),
        songKey,
        bpm: bpm ? Number(bpm) : null,
        notes,
      });

      setSelectedSongId("");
      setSongKey("");
      setBpm("");
      setNotes("");
      setIsRepertoireModalOpen(false);
      selectBand(selectedBand);
    } catch (err) {
      console.error("Erro ao adicionar música ao repertório:", err);
    }
  };

  return (
    <div className="space-y-8">
      {/* Cabeçalho */}
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-bold text-slate-100">
            As Minhas Bandas
          </h2>
          <p className="text-slate-400 text-sm">
            Gere os teus projetos musicais e os seus repertórios específicos
          </p>
        </div>
        <button
          onClick={() => setIsBandModalOpen(true)}
          className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium rounded-lg transition"
        >
          + Nova Banda
        </button>
      </div>

      {loading ? (
        <p className="text-slate-400 text-center py-8">A carregar bandas...</p>
      ) : bands.length === 0 ? (
        <div className="p-8 border border-dashed border-slate-700 rounded-xl text-center">
          <p className="text-slate-400">
            Ainda não tens nenhuma banda registada.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          {/* Selector de Bandas na Lateral */}
          <div className="space-y-2">
            <h3 className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-3">
              Projetos
            </h3>
            {bands.map((band) => (
              <button
                key={band.id}
                onClick={() => selectBand(band)}
                className={`w-full text-left p-4 rounded-xl border transition ${
                  selectedBand?.id === band.id
                    ? "bg-indigo-600/10 border-indigo-500 text-indigo-400 font-semibold"
                    : "bg-slate-900 border-slate-800 text-slate-300 hover:bg-slate-800"
                }`}
              >
                <div className="font-medium text-slate-100">{band.name}</div>
                {band.description && (
                  <div className="text-xs text-slate-400 mt-1 truncate">
                    {band.description}
                  </div>
                )}
              </button>
            ))}
          </div>

          {/* Detalhe e Repertório da Banda Selecionada */}
          {selectedBand && (
            <div className="md:col-span-3 bg-slate-900 border border-slate-800 rounded-2xl p-6 space-y-6">
              <div className="flex justify-between items-start border-b border-slate-800 pb-4">
                <div>
                  <h3 className="text-2xl font-bold text-slate-100">
                    {selectedBand.name}
                  </h3>
                  <p className="text-slate-400 text-sm">
                    {selectedBand.description || "Sem descrição"}
                  </p>
                </div>
                <button
                  onClick={() => setIsRepertoireModalOpen(true)}
                  className="px-3 py-1.5 bg-indigo-600/20 hover:bg-indigo-600/30 text-indigo-400 border border-indigo-500/30 text-sm font-medium rounded-lg transition"
                >
                  + Adicionar ao Repertório
                </button>
              </div>

              {/* Tabela de Repertório */}
              <div>
                <h4 className="text-sm font-semibold text-slate-300 mb-4">
                  Repertório da Banda
                </h4>
                {repertoire.length === 0 ? (
                  <p className="text-slate-500 text-sm italic py-4">
                    Esta banda ainda não tem músicas no repertório.
                  </p>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="w-full text-left text-sm">
                      <thead>
                        <tr className="border-b border-slate-800 text-slate-400 text-xs uppercase">
                          <th className="py-3 px-2">Música</th>
                          <th className="py-3 px-2">Artista</th>
                          <th className="py-3 px-2">Tom</th>
                          <th className="py-3 px-2">BPM</th>
                          <th className="py-3 px-2">Notas</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-slate-800/50">
                        {repertoire.map((item) => (
                          <tr key={item.id} className="text-slate-200">
                            <td className="py-3 px-2 font-medium text-slate-100">
                              {item.song?.title || "Sem título"}
                            </td>
                            <td className="py-3 px-2 text-slate-400">
                              {item.song?.artist || "Desconhecido"}
                            </td>
                            <td className="py-3 px-2">
                              <span className="px-2 py-0.5 bg-slate-800 border border-slate-700 rounded text-xs font-mono text-indigo-300">
                                {item.songKey || "-"}
                              </span>
                            </td>
                            <td className="py-3 px-2 font-mono text-xs">
                              {item.bpm || "-"}
                            </td>
                            <td className="py-3 px-2 text-slate-400 text-xs italic">
                              {item.notes || "-"}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Modal Criar Banda */}
      {isBandModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              Nova Banda
            </h3>
            <form onSubmit={handleCreateBand} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Nome da Banda
                </label>
                <input
                  type="text"
                  required
                  value={bandName}
                  onChange={(e) => setBandName(e.target.value)}
                  placeholder="Ex: Smoodies"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                  Descrição / Género
                </label>
                <input
                  type="text"
                  value={bandDesc}
                  onChange={(e) => setBandDesc(e.target.value)}
                  placeholder="Ex: Soul, Funk & Pop"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500"
                />
              </div>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setIsBandModalOpen(false)}
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

      {/* Modal Adicionar ao Repertório */}
      {isRepertoireModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">
              Adicionar Música ao Repertório
            </h3>
            <form onSubmit={handleAddToRepertoire} className="space-y-4">
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
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500"
                >
                  <option value="">Seleciona uma música...</option>
                  {globalSongs.map((song) => (
                    <option key={song.id} value={song.id}>
                      {song.title} - {song.artist}
                    </option>
                  ))}
                </select>
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
                    placeholder="Ex: Abm"
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500"
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
                    className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500"
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
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500"
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
                  Adicionar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

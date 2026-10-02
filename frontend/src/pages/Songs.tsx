import React, { useEffect, useState } from 'react';
import api from '../api/axios';

interface Song {
  id: number;
  title: string;
  artist: string;
}

export function Songs() {
  const [songs, setSongs] = useState<Song[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [title, setTitle] = useState('');
  const [artist, setArtist] = useState('');

  const fetchSongs = async () => {
    try {
      const response = await api.get('/songs');
      setSongs(response.data);
    } catch (err) {
      console.error('Erro ao carregar músicas:', err);
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
      await api.post('/songs', { title, artist });
      setTitle('');
      setArtist('');
      setIsModalOpen(false);
      fetchSongs();
    } catch (err) {
      console.error('Erro ao criar música:', err);
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h2 className="text-2xl font-bold text-slate-100">Catálogo Global de Músicas</h2>
          <p className="text-slate-400 text-sm">Biblioteca base de temas para associar às tuas bandas</p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium rounded-lg transition"
        >
          + Nova Música
        </button>
      </div>

      {loading ? (
        <p className="text-slate-400 text-center py-8">A carregar músicas...</p>
      ) : songs.length === 0 ? (
        <div className="p-8 border border-dashed border-slate-700 rounded-xl text-center">
          <p className="text-slate-400">Ainda não tens nenhuma música registada na tua biblioteca.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {songs.map((song) => (
            <div key={song.id} className="bg-slate-900 border border-slate-800 p-5 rounded-xl">
              <h3 className="font-semibold text-slate-100 text-lg">{song.title}</h3>
              <p className="text-slate-400 text-sm mt-1">{song.artist}</p>
            </div>
          ))}
        </div>
      )}

      {/* Modal de Criação */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl w-full max-w-md">
            <h3 className="text-xl font-bold text-slate-100 mb-4">Adicionar ao Catálogo</h3>
            <form onSubmit={handleCreateSong} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Título</label>
                <input
                  type="text"
                  required
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Ex: Valerie"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Artista</label>
                <input
                  type="text"
                  required
                  value={artist}
                  onChange={(e) => setArtist(e.target.value)}
                  placeholder="Ex: Amy Winehouse"
                  className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-slate-100 focus:outline-none focus:border-indigo-500"
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
import { useContext } from 'react';
import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

export function MainLayout() {
  const { logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Função auxiliar para destacar o link ativo na sidebar
  const isActive = (path: string) => location.pathname === path;

  return (
    <div className="flex h-screen bg-slate-950 text-slate-100 font-sans">
      
      {/* Sidebar */}
      <aside className="w-64 bg-slate-900 border-r border-slate-800 flex flex-col">
        <div className="h-16 flex items-center px-6 border-b border-slate-800">
          <h1 className="text-2xl font-extrabold text-indigo-500 tracking-tight">SetWist</h1>
        </div>
        
        <nav className="flex-1 p-4 space-y-1">
          <Link 
            to="/" 
            className={`block px-4 py-2 rounded-lg transition ${isActive('/') ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'}`}
          >
            Visão Geral
          </Link>
          <Link 
            to="/bandas" 
            className={`block px-4 py-2 rounded-lg transition ${isActive('/bandas') ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'}`}
          >
            Bandas
          </Link>
          <Link 
            to="/musicas" 
            className={`block px-4 py-2 rounded-lg transition ${isActive('/musicas') ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'}`}
          >
            Músicas
          </Link>
          <Link 
            to="/setlists" 
            className={`block px-4 py-2 rounded-lg transition ${isActive('/setlists') ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'}`}
          >
            Setlists
          </Link>
        </nav>
      </aside>

      {/* Área Principal */}
      <main className="flex-1 flex flex-col overflow-hidden">
        
        {/* Topbar */}
        <header className="h-16 bg-slate-900/50 border-b border-slate-800 flex items-center justify-between px-8">
          <div className="text-sm font-medium text-slate-400">
            Painel de Controlo
          </div>
          <button 
            onClick={handleLogout} 
            className="px-4 py-2 text-sm font-medium text-red-400 hover:bg-red-500/10 rounded-lg transition"
          >
            Terminar Sessão
          </button>
        </header>

        {/* Conteúdo Dinâmico (onde as páginas vão ser renderizadas) */}
        <div className="flex-1 overflow-y-auto p-8">
          <div className="max-w-5xl mx-auto">
            <Outlet />
          </div>
        </div>
        
      </main>
    </div>
  );
}
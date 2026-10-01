import { useContext, type ReactNode } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthContext } from './context/AuthContext';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { MainLayout } from './layouts/MainLayout';
import { DashboardHome } from './pages/DashboardHome';
import { Bands } from './pages/Bands';
import { Songs } from './pages/Songs';       // <-- Adicionar import
import { Setlists } from './pages/Setlists'; // <-- Adicionar import

function ProtectedRoute({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useContext(AuthContext);
  return isAuthenticated ? children : <Navigate to="/login" replace />;
}

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        <Route 
          path="/" 
          element={
            <ProtectedRoute>
              <MainLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<DashboardHome />} />
          <Route path="bandas" element={<Bands />} />
          <Route path="musicas" element={<Songs />} />       {/* <-- Rota ativa */}
          <Route path="setlists" element={<Setlists />} />   {/* <-- Rota ativa */}
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
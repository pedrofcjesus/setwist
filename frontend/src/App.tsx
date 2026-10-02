import { useContext, type ReactNode } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthContext } from "./context/AuthContext";
import { Login } from "./pages/Login";
import { Register } from "./pages/Register";
import { MainLayout } from "./layouts/MainLayout";
import { DashboardHome } from "./pages/DashboardHome";
import { Bands } from "./pages/Bands";
import { BandDetail } from "./pages/BandDetail";
import { Songs } from "./pages/Songs";
import { Setlists } from "./pages/Setlists";

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
          <Route path="bandas/:id" element={<BandDetail />} />
          <Route path="musicas" element={<Songs />} />
          <Route path="setlists/:id" element={<Setlists />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;

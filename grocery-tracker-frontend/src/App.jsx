import './App.css'
import LandingPage from './pages/LandingPage'
import Login from './pages/Login'
import Signup from './pages/Singup'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Dashboard from './pages/Dashboard'
import AddItem from './pages/AddItem'
import Navbar from './components/Navbar'
import DashBoardLayout from './components/DashBoardLayout'
import FreshItems from './pages/FreshItems'
import ExpiringItems from './pages/ExpiringItems'
import ExpiredItems from './pages/ExpiredItems'
import Reports from './pages/Reports'
import Settings from './pages/Settings'
import Profile from './pages/Profile'
import RemedySuggestions from './pages/RemedySuggestions'
import { InventoryProvider } from './context/InventoryContext';

function App() {

  return (
    <BrowserRouter>
      <InventoryProvider>

        <Navbar notifications />

        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/Signup" element={<Signup />} />
          <Route path="/login" element={<Login />} />

          <Route path="/dashboard" element={<DashBoardLayout />}>
            <Route index element={<Dashboard />} />
            <Route path="reports" element={<Reports />} />
            <Route path="settings" element={<Settings />} />
            <Route path="remedies" element={<RemedySuggestions />} />
          </Route>

          <Route path="/dashboard/profile" element={<Profile />} />

          <Route
            path="/dashboard/inventory/fresh"
            element={<FreshItems />}
          />

          <Route
            path="/dashboard/inventory/expiring"
            element={<ExpiringItems />}
          />

          <Route
            path="/dashboard/inventory/expired"
            element={<ExpiredItems />}
          />

          <Route path="/dashboard/addItem" element={<AddItem />} />

          <Route path="*" element={<h1>Page Not Found</h1>} />
        </Routes>

      </InventoryProvider>
    </BrowserRouter>
  )
}

export default App
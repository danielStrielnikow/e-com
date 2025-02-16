import { useState } from "react";
import "./App.css";
import Products from "./componets/products/Products";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./componets/home/Home";
import NavBar from "./componets/shared/NavBar";
import About from "./componets/About";
import Contact from "./componets/Contact";
import List from "./componets/List";

function App() {
  const [count, setCount] = useState(0);

  return (
    <Router>
      <NavBar/>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/products" element={<Products />} />
        <Route path="/about" element={<About/>} />
        <Route path="/contact" element={<Contact/>} />
        <Route path="/list" element={<List/>} />
      </Routes>
    </Router>
  );
}

export default App;

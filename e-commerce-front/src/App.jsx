import { useState } from 'react'
import './App.css'
import Products from './componets/products/Products'


function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <Products />
    </>
  )
}

export default App

import { useState } from 'react'
import './App.css'
import { FaBeer, FaGithub } from 'react-icons/fa'
import { MdDownloading } from 'react-icons/md'

function App() {
  const [count, setCount] = useState(0)

  return (
    <div>
      Welcome<FaGithub/>
    </div>
  )
}

export default App

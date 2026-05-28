import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Upload from './Components/Upload'
import './App.css'
import Query from './Components/Query'

function App() {
  

  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path='/' element={<Upload/>} />
          <Route path='/query' element={<Query/>} />
        </Routes>
      </BrowserRouter>

    </>
  )
}

export default App

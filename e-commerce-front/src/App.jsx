import React, { Suspense } from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Navbar from './components/shared/NavBar'
import { Toaster } from 'react-hot-toast'
import PrivateRoute from './components/PrivateRoute'

const Home = React.lazy(() => import('./components/home/Home'))
const Products = React.lazy(() => import('./components/products/Products'))
const About = React.lazy(() => import('./components/About'))
const Contact = React.lazy(() => import('./components/Contact'))
const Cart = React.lazy(() => import('./components/cart/Cart'))
const LogIn = React.lazy(() => import('./components/auth/Login'))
const Register = React.lazy(() => import('./components/auth/Register'))
const Checkout = React.lazy(() => import('./components/checkout/Checkout'))
const PaymentConfirmation = React.lazy(() => import('./components/checkout/PaymentConfirmation'))
const FavoritesList = React.lazy(() => import('./components/shared/FavoritesList'))

function App() {
  return (
    <React.Fragment>
      <Router>
        <Navbar />
        <Suspense fallback={<div>Loading...</div>}>
          <Routes>
            <Route path='/' element={<Home />} />
            <Route path='/products' element={<Products />} />
            <Route path='/about' element={<About />} />
            <Route path='/contact' element={<Contact />} />
            <Route path='/list' element={<FavoritesList />} />
            <Route path='/cart' element={<Cart />} />

            <Route path='/' element={<PrivateRoute />}>
              <Route path='/checkout' element={<Checkout />} />
              <Route path='/order-confirm' element={<PaymentConfirmation />} />
            </Route>

            <Route path='/' element={<PrivateRoute publicPage />}>
              <Route path='/login' element={<LogIn />} />
              <Route path='/register' element={<Register />} />
            </Route>
          </Routes>
        </Suspense>
      </Router>
      <Toaster position='bottom-center' />
    </React.Fragment>
  )
}

export default App

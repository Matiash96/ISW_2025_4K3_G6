//import { useState } from 'react'

import './App.css'
import './styles/globals.css'
import ActivityRegistrationForm from "./components/ActivityRegistrationForm"

function App() {

  return (
    <>
      <div className="min-h-screen bg-background">
        <header className="bg-primary text-primary-foreground py-6 shadow-md">
          <div className="container mx-auto px-4">
            <h1 className="text-3xl font-bold text-center text-balance">EcoHarmony Park</h1>
          </div>
        </header>

        <main className="container mx-auto px-4 py-8 max-w-2xl">
          <ActivityRegistrationForm />
        </main>
      </div>
    </>
  )
}

export default App

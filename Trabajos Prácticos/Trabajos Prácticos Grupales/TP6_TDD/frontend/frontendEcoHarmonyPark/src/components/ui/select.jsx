"use client"

import { useState, createContext, useContext } from "react"

const SelectContext = createContext()

export function Select({ value, onValueChange, children }) {
  const [isOpen, setIsOpen] = useState(false)
  const [internalValue, setInternalValue] = useState(value || "")

  const handleValueChange = (newValue) => {
    setInternalValue(newValue)
    onValueChange?.(newValue)
    setIsOpen(false)
  }

  return (
    <SelectContext.Provider value={{ value: internalValue, onValueChange: handleValueChange, isOpen, setIsOpen }}>
      <div className="relative">{children}</div>
    </SelectContext.Provider>
  )
}

export function SelectTrigger({ className = "", children, ...props }) {
  const { isOpen, setIsOpen } = useContext(SelectContext)

  return (
    <button
      type="button"
      onClick={() => setIsOpen(!isOpen)}
      className={`flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${className}`}
      {...props}
    >
      {children}
      <svg
        className={`h-4 w-4 opacity-50 transition-transform ${isOpen ? "rotate-180" : ""}`}
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
      </svg>
    </button>
  )
}

export function SelectValue({ placeholder }) {
  const { value } = useContext(SelectContext)
  return <span className={value ? "" : "text-muted-foreground"}>{value || placeholder}</span>
}

export function SelectContent({ children }) {
  const { isOpen } = useContext(SelectContext)

  if (!isOpen) return null

  return (
    <div className="absolute z-50 mt-1 w-full rounded-md border bg-popover text-popover-foreground shadow-md animate-in fade-in-80">
      <div className="max-h-60 overflow-auto p-1">{children}</div>
    </div>
  )
}

export function SelectItem({ value, disabled, children, ...props }) {
  const { onValueChange, value: selectedValue } = useContext(SelectContext)
  const isSelected = selectedValue === value

  return (
    <div
      onClick={() => !disabled && onValueChange(value)}
      className={`relative flex w-full cursor-pointer select-none items-center rounded-sm py-2 px-2 text-sm outline-none transition-colors ${
        disabled
          ? "pointer-events-none opacity-50"
          : "hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground"
      } ${isSelected ? "bg-accent" : ""}`}
      {...props}
    >
      {children}
    </div>
  )
}

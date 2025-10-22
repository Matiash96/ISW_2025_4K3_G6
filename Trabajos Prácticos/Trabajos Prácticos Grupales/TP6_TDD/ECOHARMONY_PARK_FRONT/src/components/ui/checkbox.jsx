"use client"

export function Checkbox({ className = "", checked, onCheckedChange, ...props }) {
  const handleChange = (e) => {
    onCheckedChange?.(e.target.checked)
  }

  return (
    <input
      type="checkbox"
      checked={checked}
      onChange={handleChange}
      className={`h-4 w-4 rounded border-input text-primary focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer ${className}`}
      {...props}
    />
  )
}

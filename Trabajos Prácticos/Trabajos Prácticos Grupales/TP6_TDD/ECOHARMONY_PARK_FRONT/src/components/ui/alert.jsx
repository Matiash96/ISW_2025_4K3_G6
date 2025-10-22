export function Alert({ className = "", variant = "default", children, ...props }) {
  const variants = {
    default: "bg-background text-foreground border-border",
    destructive: "bg-destructive/10 text-destructive border-destructive/50",
  }

  return (
    <div
      role="alert"
      className={`relative w-full rounded-lg border p-4 flex items-start gap-3 ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </div>
  )
}

export function AlertDescription({ className = "", children, ...props }) {
  return (
    <div className={`text-sm leading-relaxed ${className}`} {...props}>
      {children}
    </div>
  )
}

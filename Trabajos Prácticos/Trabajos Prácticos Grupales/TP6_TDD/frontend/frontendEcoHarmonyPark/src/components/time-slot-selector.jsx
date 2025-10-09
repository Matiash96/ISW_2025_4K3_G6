"use client"

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../components/ui/select"

export default function TimeSlotSelector({ slots, selectedSlot, onSlotChange }) {
  return (
    <Select value={selectedSlot} onValueChange={onSlotChange}>
      <SelectTrigger className="w-full text-base">
        <SelectValue placeholder="Seleccione un horario" />
      </SelectTrigger>
      <SelectContent>
        {Object.entries(slots).map(([time, available]) => (
          <SelectItem key={time} value={time} disabled={available === 0}>
            <div className="flex items-center justify-between w-full gap-4">
              <span>{time}</span>
              <span className={`text-xs ${available > 0 ? "text-primary" : "text-destructive"}`}>
                {available > 0 ? `${available} cupos disponibles` : "Sin cupos"}
              </span>
            </div>
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  )
}

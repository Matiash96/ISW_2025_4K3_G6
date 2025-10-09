"use client"

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../components/ui/select"

export default function ActivitySelector({ activities, selectedActivity, onActivityChange }) {
  return (
    <Select value={selectedActivity || ""} onValueChange={onActivityChange}>
      <SelectTrigger className="w-full text-base">
        <SelectValue placeholder="Seleccione una actividad" />
      </SelectTrigger>
      <SelectContent>
        {activities.map((activity) => {
          const totalSlots = Object.values(activity.availableSlots).reduce((a, b) => a + b, 0)
          const hasAvailability = totalSlots > 0

          return (
            <SelectItem key={activity.id} value={activity.id} disabled={!hasAvailability}>
              <div className="flex items-center justify-between w-full">
                <span>{activity.name}</span>
                {!hasAvailability && <span className="text-xs text-muted-foreground ml-2">(Sin cupos)</span>}
              </div>
            </SelectItem>
          )
        })}
      </SelectContent>
    </Select>
  )
}

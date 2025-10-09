"use client"

import { Label } from "../components/ui/label"
import { Input } from "../components/ui/input"
import { Button } from "../components/ui/button"
//import { ChevronLeft, ChevronRight } from "./lib/icons"

export default function ParticipantForm({
  participant,
  participantIndex,
  totalParticipants,
  requiresClothingSize,
  onDataChange,
  onPrevious,
  onNext,
  errors,
}) {
  return (
    <div className="space-y-4 p-4 bg-muted/30 rounded-lg border border-border">
      <div className="flex items-center justify-between mb-4">
        <Button type="button" variant="outline" size="icon" onClick={onPrevious} disabled={participantIndex === 0}>
          {"<"}
        </Button>

        <p className="text-sm font-semibold">
          Persona {participantIndex + 1} de {totalParticipants}
        </p>

        <Button
          type="button"
          variant="outline"
          size="icon"
          onClick={onNext}
          disabled={participantIndex === totalParticipants - 1}
        >
         {">"}
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="space-y-2">
          <Label htmlFor={`name-${participantIndex}`}>Nombre Completo</Label>
          <Input
            id={`name-${participantIndex}`}
            type="text"
            value={participant.name}
            onChange={(e) => onDataChange(participantIndex, "name", e.target.value)}
            placeholder="Ingrese nombre completo"
            className={errors[`participant_${participantIndex}_name`] ? "border-destructive" : ""}
          />
          {errors[`participant_${participantIndex}_name`] && (
            <p className="text-xs text-destructive">{errors[`participant_${participantIndex}_name`]}</p>
          )}
        </div>

        <div className="space-y-2">
          <Label htmlFor={`dni-${participantIndex}`}>DNI</Label>
          <Input
            id={`dni-${participantIndex}`}
            type="text"
            value={participant.dni}
            onChange={(e) => onDataChange(participantIndex, "dni", e.target.value)}
            placeholder="Ingrese DNI"
            className={errors[`participant_${participantIndex}_dni`] ? "border-destructive" : ""}
          />
          {errors[`participant_${participantIndex}_dni`] && (
            <p className="text-xs text-destructive">{errors[`participant_${participantIndex}_dni`]}</p>
          )}
        </div>

        <div className="space-y-2">
          <Label htmlFor={`age-${participantIndex}`}>Edad</Label>
          <Input
            id={`age-${participantIndex}`}
            type="number"
            min="1"
            max="120"
            value={participant.age}
            onChange={(e) => onDataChange(participantIndex, "age", e.target.value)}
            placeholder="Ingrese edad"
            className={errors[`participant_${participantIndex}_age`] ? "border-destructive" : ""}
          />
          {errors[`participant_${participantIndex}_age`] && (
            <p className="text-xs text-destructive">{errors[`participant_${participantIndex}_age`]}</p>
          )}
        </div>

        {requiresClothingSize && (
          <div className="space-y-2">
            <Label htmlFor={`clothingSize-${participantIndex}`}>Talla</Label>
            <Input
              id={`clothingSize-${participantIndex}`}
              type="text"
              value={participant.clothingSize}
              onChange={(e) => onDataChange(participantIndex, "clothingSize", e.target.value)}
              placeholder="Ej: S, M, L, XL"
              className={errors[`participant_${participantIndex}_clothingSize`] ? "border-destructive" : ""}
            />
            {errors[`participant_${participantIndex}_clothingSize`] && (
              <p className="text-xs text-destructive">{errors[`participant_${participantIndex}_clothingSize`]}</p>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

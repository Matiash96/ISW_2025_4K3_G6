"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../components/ui/card"
import { Label } from "../components/ui/label"
import { Input } from "../components/ui/input"
import { Button } from "../components/ui/button"
import { Checkbox } from "../components/ui/checkbox"
import { Alert, AlertDescription } from "../components/ui/alert"
import ActivitySelector from "./activity-selector"
import TimeSlotSelector from "./time-slot-selector"
import ParticipantForm from "./participant-form"
//import { AlertCircle, CheckCircle2 } from "./lib/icons"

// Mock data de actividades con sus configuraciones
const ACTIVITIES = [
  {
    id: "tirolesa",
    name: "Tirolesa",
    requiresClothingSize: true,
    availableSlots: {
      "09:00": 10,
      "11:00": 5,
      "14:00": 0,
      "16:00": 8,
    },
    terms:
      "Al participar en la actividad de Tirolesa, acepto que he leído y comprendo los riesgos asociados. Me comprometo a seguir todas las instrucciones de seguridad proporcionadas por el personal.",
  },
  {
    id: "safari",
    name: "Safari",
    requiresClothingSize: false,
    availableSlots: {
      "10:00": 12,
      "13:00": 6,
      "15:00": 3,
      "17:00": 0,
    },
    terms:
      "Al participar en el Safari, acepto respetar a los animales y seguir las indicaciones del guía en todo momento. No está permitido alimentar a los animales.",
  },
  {
    id: "palestra",
    name: "Palestra",
    requiresClothingSize: true,
    availableSlots: {
      "08:00": 8,
      "10:00": 4,
      "12:00": 0,
      "15:00": 6,
    },
    terms:
      "Al participar en la Palestra, acepto que estoy en condiciones físicas adecuadas y seguiré todas las medidas de seguridad. El uso del equipo de protección es obligatorio.",
  },
  {
    id: "jardineria",
    name: "Jardinería",
    requiresClothingSize: false,
    availableSlots: {
      "09:00": 15,
      "11:00": 10,
      "14:00": 8,
      "16:00": 5,
    },
    terms:
      "Al participar en la actividad de Jardinería, acepto cuidar las plantas y herramientas proporcionadas. Me comprometo a seguir las instrucciones del instructor.",
  },
]

export default function ActivityRegistrationForm() {
  const [selectedActivity, setSelectedActivity] = useState(null)
  const [selectedTimeSlot, setSelectedTimeSlot] = useState("")
  const [numberOfParticipants, setNumberOfParticipants] = useState(1)
  const [currentParticipantIndex, setCurrentParticipantIndex] = useState(0)
  const [participants, setParticipants] = useState([
    {
      name: "",
      dni: "",
      age: "",
      clothingSize: "",
    },
  ])
  const [termsAccepted, setTermsAccepted] = useState(false)
  const [errors, setErrors] = useState({})
  const [submitStatus, setSubmitStatus] = useState(null)

  const handleActivityChange = (activityId) => {
    const activity = ACTIVITIES.find((a) => a.id === activityId)
    setSelectedActivity(activity)
    setSelectedTimeSlot("")
    setErrors({})
  }

  const handleNumberOfParticipantsChange = (value) => {
    const num = Number.parseInt(value) || 1
    setNumberOfParticipants(num)

    // Ajustar el array de participantes
    const newParticipants = [...participants]
    if (num > participants.length) {
      for (let i = participants.length; i < num; i++) {
        newParticipants.push({
          name: "",
          dni: "",
          age: "",
          clothingSize: "",
        })
      }
    } else {
      newParticipants.splice(num)
    }
    setParticipants(newParticipants)

    // Ajustar el índice actual si es necesario
    if (currentParticipantIndex >= num) {
      setCurrentParticipantIndex(num - 1)
    }
  }

  const handleParticipantDataChange = (index, field, value) => {
    const newParticipants = [...participants]
    newParticipants[index][field] = value
    setParticipants(newParticipants)
  }

  const validateForm = () => {
    const newErrors = {}

    if (!selectedActivity) {
      newErrors.activity = "Debe seleccionar una actividad"
    }

    if (!selectedTimeSlot) {
      newErrors.timeSlot = "Debe seleccionar un horario"
    } else if (selectedActivity && selectedActivity.availableSlots[selectedTimeSlot] === 0) {
      newErrors.timeSlot = "El horario seleccionado no tiene cupos disponibles"
    }

    if (numberOfParticipants < 1) {
      newErrors.participants = "Debe haber al menos un participante"
    }

    // Validar datos de cada participante
    participants.forEach((participant, index) => {
      if (!participant.name.trim()) {
        newErrors[`participant_${index}_name`] = "El nombre es requerido"
      }
      if (!participant.dni.trim()) {
        newErrors[`participant_${index}_dni`] = "El DNI es requerido"
      }
      if (!participant.age || participant.age < 1) {
        newErrors[`participant_${index}_age`] = "La edad es requerida"
      }
      if (selectedActivity?.requiresClothingSize && !participant.clothingSize.trim()) {
        newErrors[`participant_${index}_clothingSize`] = "La talla de vestimenta es requerida para esta actividad"
      }
    })

    if (!termsAccepted) {
      newErrors.terms = "Debe aceptar los términos y condiciones"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = (e) => {
    e.preventDefault()

    if (validateForm()) {
      // Aquí iría la lógica para enviar los datos al servidor
      console.log("Formulario válido:", {
        activity: selectedActivity,
        timeSlot: selectedTimeSlot,
        participants,
      })
      setSubmitStatus("success")

      // Resetear formulario después de 3 segundos
      setTimeout(() => {
        setSelectedActivity(null)
        setSelectedTimeSlot("")
        setNumberOfParticipants(1)
        setParticipants([{ name: "", dni: "", age: "", clothingSize: "" }])
        setCurrentParticipantIndex(0)
        setTermsAccepted(false)
        setSubmitStatus(null)
      }, 3000)
    } else {
      setSubmitStatus("error")
    }
  }

  const availableSlots = selectedActivity?.availableSlots || {}
  const slotsWithAvailability = Object.entries(availableSlots).filter(([_, count]) => count > 0)

  return (
    <Card className="shadow-lg">
      <CardHeader className="bg-primary/5 border-b border-border">
        <CardTitle className="text-2xl text-center text-balance">Inscripción a Actividad</CardTitle>
        <CardDescription className="text-center text-pretty">
          Complete el formulario para reservar su lugar en nuestras actividades
        </CardDescription>
      </CardHeader>

      <CardContent className="pt-6">
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Selector de Actividad */}
          <div className="space-y-2">
            <Label htmlFor="activity">Seleccione una actividad</Label>
            <ActivitySelector
              activities={ACTIVITIES}
              selectedActivity={selectedActivity?.id}
              onActivityChange={handleActivityChange}
            />
            {errors.activity && <p className="text-sm text-destructive">{errors.activity}</p>}
          </div>

          {/* Selector de Horario */}
          {selectedActivity && (
            <div className="space-y-2">
              <Label htmlFor="timeSlot">Seleccione un horario</Label>
              <TimeSlotSelector
                slots={availableSlots}
                selectedSlot={selectedTimeSlot}
                onSlotChange={setSelectedTimeSlot}
              />
              {errors.timeSlot && <p className="text-sm text-destructive">{errors.timeSlot}</p>}
              {slotsWithAvailability.length === 0 && (
                

                <p>{/*<Alert variant="destructive">
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription>No hay horarios disponibles para esta actividad</AlertDescription>
                </Alert>*/}Alertaaaa</p>
              )}
            </div>
          )}

          {/* Cantidad de Personas */}
          <div className="space-y-2">
            <Label htmlFor="numberOfParticipants" className="uppercase text-xs font-semibold tracking-wide">
              Ingrese la cantidad de personas
            </Label>
            <Input
              id="numberOfParticipants"
              type="number"
              min="1"
              max="10"
              value={numberOfParticipants}
              onChange={(e) => handleNumberOfParticipantsChange(e.target.value)}
              className="text-base"
            />
            {errors.participants && <p className="text-sm text-destructive">{errors.participants}</p>}
          </div>

          {/* Datos de los Participantes */}
          <div className="space-y-4">
            <Label className="text-base font-semibold">Datos de los participantes</Label>

            <ParticipantForm
              participant={participants[currentParticipantIndex]}
              participantIndex={currentParticipantIndex}
              totalParticipants={numberOfParticipants}
              requiresClothingSize={selectedActivity?.requiresClothingSize || false}
              onDataChange={handleParticipantDataChange}
              onPrevious={() => setCurrentParticipantIndex(Math.max(0, currentParticipantIndex - 1))}
              onNext={() => setCurrentParticipantIndex(Math.min(numberOfParticipants - 1, currentParticipantIndex + 1))}
              errors={errors}
            />
          </div>

          {/* Términos y Condiciones */}
          {selectedActivity && (
            <div className="space-y-3 p-4 bg-muted/50 rounded-lg">
              <div className="flex items-start gap-3">
                <Checkbox id="terms" checked={termsAccepted} onCheckedChange={setTermsAccepted} className="mt-1" />
                <div className="space-y-2">
                  <Label htmlFor="terms" className="text-sm font-medium leading-relaxed cursor-pointer">
                    Acepto los términos y condiciones
                  </Label>
                  <p className="text-sm text-muted-foreground leading-relaxed">{selectedActivity.terms}</p>
                </div>
              </div>
              {errors.terms && <p className="text-sm text-destructive">{errors.terms}</p>}
            </div>
          )}

          {/* Mensajes de Estado */}
          {submitStatus === "success" && (
            <Alert className="bg-primary/10 border-primary">
             {/* <CheckCircle2 className="h-4 w-4 text-primary" />*/}
              <AlertDescription className="text-primary">
                ¡Inscripción exitosa! Su reserva ha sido confirmada.
              </AlertDescription>
            </Alert>
          )}

          {submitStatus === "error" && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>Por favor, corrija los errores en el formulario antes de continuar.</AlertDescription>
            </Alert>
          )}

          {/* Botón de Envío */}
          <Button type="submit" className="w-full text-base font-semibold py-6" size="lg">
            Enviar
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}

import React, { useEffect, useState } from "react";
import ActivitySelector from "./ActivitySelector";
import TimeSlotSelector from "./TimeSlotSelector";
import ParticipantForm from "./ParticipantForm";
import "../styles/ActivityForm.scss";

const ACTIVITIES = [
  {
    id: "tirolesa",
    name: "Tirolesa",
    requiresClothingSize: true,
    availableSlots: { "09:00": 10, "11:00": 5, "14:00": 0, "16:00": 8 },
    terms:
      "Al participar en la actividad de Tirolesa, acepto los riesgos y seguiré las instrucciones del personal.",
  },
  {
    id: "safari",
    name: "Safari",
    requiresClothingSize: false,
    availableSlots: { "10:00": 12, "13:00": 6, "15:00": 3, "17:00": 0 },
    terms:
      "Durante el Safari, respetaré a los animales y seguiré las indicaciones del guía.",
  },
  {
    id: "palestra",
    name: "Palestra",
    requiresClothingSize: true,
    availableSlots: { "08:00": 8, "10:00": 4, "12:00": 0, "15:00": 6 },
    terms:
      "En la Palestra usaré el equipo de protección obligatorio y seguiré las normas de seguridad.",
  },
  {
    id: "jardineria",
    name: "Jardinería",
    requiresClothingSize: true,
    availableSlots: { "08:00": 0},
    terms:
      "En la Jardinería usaré el equipo de protección obligatorio y seguiré las normas de seguridad.",
  },
];

export default function ActivityRegistrationForm() {
  const [selectedActivity, setSelectedActivity] = useState(null);
  const [selectedTimeSlot, setSelectedTimeSlot] = useState("");
  const [numberOfParticipants, setNumberOfParticipants] = useState(1);
  const [participants, setParticipants] = useState([
    { name: "", dni: "", age: "", clothingSize: "" },
  ]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [errors, setErrors] = useState({});
  const [status, setStatus] = useState(null);

  const handleActivityChange = (id) => {
    const act = ACTIVITIES.find((a) => a.id === id);
    setSelectedActivity(act);
    setSelectedTimeSlot("");
    setErrors({});
  };

  const handleParticipantChange = (index, field, value) => {
    const updated = [...participants];
    updated[index][field] = value;
    setParticipants(updated);
  };

  const handleNumChange = (value) => {
    const num = Math.max(1, parseInt(value) || 1);
    const updated = [...participants];
    if (num > participants.length) {
      for (let i = participants.length; i < num; i++) {
        updated.push({ name: "", dni: "", age: "", clothingSize: "" });
      }
    } else {
      updated.splice(num);
    }
    setParticipants(updated);
    setNumberOfParticipants(num);
  };

  const validate = () => {
    const newErrors = {};
    if (!selectedActivity) newErrors.activity = "Seleccione una actividad";
    if (!selectedTimeSlot) newErrors.time = "Seleccione un horario";
    if (!termsAccepted) newErrors.terms = "Debe aceptar los términos";

    participants.forEach((p, i) => {
      if (!p.name.trim()) newErrors[`name_${i}`] = "Nombre requerido";
      if (!p.dni.trim()) newErrors[`dni_${i}`] = "DNI requerido";
      if (!p.age) newErrors[`age_${i}`] = "Edad requerida";
      if (selectedActivity?.requiresClothingSize && !p.clothingSize)
        newErrors[`size_${i}`] = "Talle requerido";
    });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const validateParticipant = (participant) => {
    const newErrors = {};
    
      if (!participant.name.trim()) newErrors[`name`] = "Nombre requerido";
      if (!participant.dni.trim()) newErrors[`dni`] = "DNI requerido";
      if (!participant.age) newErrors[`age`] = "Edad requerida";
      if (selectedActivity?.requiresClothingSize && !participant.clothingSize)
        newErrors[`size`] = "Talle requerido";
    

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) {
      setStatus("error");
      return;
    }

    console.log({
      activity: selectedActivity.name,
      time: selectedTimeSlot,
      participants,
    });
    setStatus("success");

    setSelectedActivity(null);
    setSelectedTimeSlot("");
    setParticipants([{ name: "", dni: "", age: "", clothingSize: "" }]);
    setCurrentIndex(0);
    setNumberOfParticipants(1);
    setTermsAccepted(false);
    setStatus(null);
  };

  return (
    <div className="container py-4">
      <div className="card shadow">
        <div className="card-header text-center bg-light">
          <h3>Inscripción a Actividad</h3>
          <p className="text-muted">
            Complete el formulario para reservar su lugar
          </p>
        </div>

        <div className="card-body">
          <form onSubmit={handleSubmit}>
            {/* Selector de actividad */}
            <ActivitySelector
              activities={ACTIVITIES}
              selectedActivity={selectedActivity}
              onChange={handleActivityChange}
              error={errors.activity}
            />

            {/* Selector de horario */}
            {selectedActivity && (
              <TimeSlotSelector
                slots={selectedActivity.availableSlots}
                selected={selectedTimeSlot}
                onChange={setSelectedTimeSlot}
                error={errors.time}
              />
            )}

            {/* Participantes */}
            <div className="mb-3">
              <label className="form-label">Cantidad de participantes</label>
              <input
                type="number"
                className="form-control"
                value={numberOfParticipants}
                min="1"
                onChange={(e) => handleNumChange(e.target.value)}
              />
            </div>

            <ParticipantForm
              participant={participants[currentIndex]}
              index={currentIndex}
              total={numberOfParticipants}
              onChange={handleParticipantChange}
              requiresClothingSize={selectedActivity?.requiresClothingSize}
              next={() => {
                 if (!validateParticipant(participants[currentIndex])) {
                    console.log("entre")
                    setStatus("errorFaltanDatos");
                    return;
                  }
                setStatus("");
                setCurrentIndex(Math.min(currentIndex + 1, numberOfParticipants - 1))
              }}
              prev={() => setCurrentIndex(Math.max(currentIndex - 1, 0))}
              errors={errors}
            />

            {/* Términos */}
            {selectedActivity && (
              <div className="form-check mt-3 mb-3">
                <input
                  type="checkbox"
                  className="form-check-input"
                  id="terms"
                  checked={termsAccepted}
                  onChange={() => setTermsAccepted(!termsAccepted)}
                />
                <label htmlFor="terms" className="form-check-label">
                  Acepto los términos y condiciones
                </label>
                <p className="text-muted small mt-1">
                  {selectedActivity.terms}
                </p>
                {errors.terms && (
                  <div className="text-danger small">{errors.terms}</div>
                )}
              </div>
            )}

            {/* Estado */}
            {status === "success" && (
              <div className="alert alert-success">
                ¡Inscripción exitosa! Su reserva ha sido confirmada.
              </div>
            )}
            {status === "error" && (
              <div className="alert alert-danger">
                Corrija los errores antes de continuar.
              </div>
            )}
            {status === "errorFaltanDatos" && (
              <div className="alert alert-danger">
                Complete todos los campos del participante
              </div>
            )}

            <button type="submit" className="btn btn-primary w-100 mt-3">
              Enviar
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

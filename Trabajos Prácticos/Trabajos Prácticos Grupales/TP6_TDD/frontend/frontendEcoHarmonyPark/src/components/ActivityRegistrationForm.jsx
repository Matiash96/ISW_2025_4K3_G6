import React, { useEffect, useState } from "react";
import ActivitySelector from "./ActivitySelector";
import TimeSlotSelector from "./TimeSlotSelector";
import ParticipantForm from "./ParticipantForm";
import "../styles/ActivityForm.scss";

import {
  getActividades,
  getProgramadas,
  getTallas,
  postInscripcion,
} from "../services/actividades.service";

export default function ActivityRegistrationForm() {
  const [activities, setActivities] = useState([]); // viene del backend
  const [tallas, setTallas] = useState([]);
  const [selectedActivity, setSelectedActivity] = useState(null);
  const [selectedTimeSlot, setSelectedTimeSlot] = useState(""); // "HH:mm"
  const [programadas, setProgramadas] = useState([]); // lista cruda para mapear id
  const [numberOfParticipants, setNumberOfParticipants] = useState(1);
  const [participants, setParticipants] = useState([
    { name: "", dni: "", age: "", clothingSize: "" },
  ]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [errors, setErrors] = useState({});
  const [status, setStatus] = useState(null);

  // Cargar catálogo y tallas al montar
  useEffect(() => {
    (async () => {
      try {
        const [acts, tallasResp] = await Promise.all([
          getActividades(),
          getTallas(),
        ]);
        setActivities(acts);
        setTallas(tallasResp);
      } catch (e) {
        console.error(e);
        setStatus("error");
      }
    })();
  }, []);

  const handleActivityChange = async (id) => {
    const act = activities.find((a) => String(a.id) === String(id));
    setSelectedActivity(null);
    setSelectedTimeSlot("");
    setErrors({});

    try {
      const prg = await getProgramadas(act.id);
      setProgramadas(prg);

      const slots = {};
      prg.forEach((p) => {
        const hhmm = new Date(p.fechaHoraInicio).toLocaleTimeString("es-AR", {
          hour: "2-digit",
          minute: "2-digit",
          hour12: false,
        });
        slots[hhmm] = p.cupoDisponible ?? p.cupo;
      });

      // ← usar el nombre DEL BACKEND
      const requires = !!act.requiereVestimenta;

      setSelectedActivity({
        ...act,
        requiresClothingSize: requires,
        availableSlots: slots,
        terminosYCondiciones: act.terminosYCondiciones ?? "",
      });

      // Si NO requiere vestimenta, limpiamos talles cargados
      if (!requires) {
        setParticipants((prev) =>
          prev.map((p) => ({ ...p, clothingSize: "" }))
        );
      }
    } catch (e) {
      console.error("Error al obtener actividades programadas:", e);
      console.error("Respuesta del servidor:", e.response?.data);
      setStatus("error");
    }
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

  // --- validaciones (igual que tenías) ---
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) {
      setStatus("error");
      return;
    }

    try {
      // Mapear el horario elegido ("HH:mm") -> id de ActividadProgramada
      const chosen = programadas.find((p) => {
        const hhmm = new Date(p.fechaHoraInicio).toLocaleTimeString("es-AR", {
          hour: "2-digit",
          minute: "2-digit",
          hour12: false,
        });
        return hhmm === selectedTimeSlot;
      });
      if (!chosen) {
        setStatus("error");
        return;
      }

      const dto = {
        fechaHoraInscripcion: new Date().toISOString().slice(0, 19), // "YYYY-MM-DDTHH:mm:ss"
        actividadProgramadaId: chosen.id,
        aceptanTerminosYCondiciones: termsAccepted,
        participantes: participants.map((p) => ({
          nombre: p.name,
          dni: Number(p.dni),
          edad: Number(p.age),
          tallaId: selectedActivity?.requiresClothingSize
            ? Number(p.clothingSize) || null
            : null,
        })),
      };

      const resp = await postInscripcion(dto);
      console.log("Inscripción OK:", resp);

      setStatus("success");
      // reset
      setSelectedActivity(null);
      setSelectedTimeSlot("");
      setParticipants([{ name: "", dni: "", age: "", clothingSize: "" }]);
      setCurrentIndex(0);
      setNumberOfParticipants(1);
      setTermsAccepted(false);
      setProgramadas([]);
    } catch (err) {
      console.error(err);
      // si el backend devuelve {error: "..."} en 400/409/500
      setStatus("error");
    }
  };

  // flags de habilitación por paso
  const canPickSlot         = !!selectedActivity;
  const canSetCount         = canPickSlot;
  const canEditParticipants = canPickSlot && !!selectedTimeSlot;
  const canAcceptTerms      = canEditParticipants;

  // para habilitar el botón Enviar sin correr validate() en cada render:
  const hasAllParticipantsFilled = participants
    .slice(0, numberOfParticipants)
    .every(p =>
      p.name?.trim() &&
      p.dni?.toString().trim() &&
      p.age &&
      (!selectedActivity?.requiresClothingSize || p.clothingSize)
    );

  const canSubmit = !!selectedActivity && !!selectedTimeSlot && termsAccepted && hasAllParticipantsFilled;

  return (
    <div className="container py-4">
      
      <div className="card shadow">
        <div className="card-header text-center bg-light">
          <h3>Inscripción a Actividad</h3>
          <p className="text-muted">Complete el formulario para reservar su lugar</p>
        </div>

        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <fieldset>
            <ActivitySelector
              activities={activities}               // ahora viene del backend
              selectedActivity={selectedActivity}
              onChange={handleActivityChange}
              error={errors.activity}
            />
            </fieldset>

            <fieldset disabled={!canPickSlot} className={!canPickSlot ? "section--disabled" : ""}>
            {selectedActivity && (
              <TimeSlotSelector
                slots={selectedActivity.availableSlots} // {"HH:mm": cupo}
                selected={selectedTimeSlot}
                onChange={setSelectedTimeSlot}
                error={errors.time}
              />
            )}
            </fieldset>

            {/* Participantes */}
            <fieldset disabled={!canSetCount} className={!canSetCount ? "section--disabled" : ""}>
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
            </fieldset>

            <fieldset disabled={!canEditParticipants} className={!canEditParticipants ? "section--disabled" : ""}>
            <ParticipantForm
              participant={participants[currentIndex]}
              index={currentIndex}
              total={numberOfParticipants}
              onChange={handleParticipantChange}
              requiresClothingSize={selectedActivity?.requiresClothingSize}
              tallas={tallas}
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
            </fieldset>

            {/* Términos */}
            <fieldset disabled={!canAcceptTerms} className={!canAcceptTerms ? "section--disabled" : ""}>
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
                  {selectedActivity.terminosYCondiciones}
                </p>
                {errors.terms && (
                  <div className="text-danger small">{errors.terms}</div>
                )}
              </div>
            )}
            </fieldset>

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

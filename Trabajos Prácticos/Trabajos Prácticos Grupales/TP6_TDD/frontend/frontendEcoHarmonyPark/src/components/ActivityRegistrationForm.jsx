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
  const [registrationData, setRegistrationData] = useState(null); // DTO de respuesta
  const [isSubmitting, setIsSubmitting] = useState(false); // Para evitar doble envío
  const [errorMessage, setErrorMessage] = useState(""); // Mensaje de error del backend

  // Función helper para obtener fecha/hora local en formato ISO
  const getLocalISOString = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  };

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
    setErrorMessage(""); // Limpiar mensaje de error del backend

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
      if (!p.dni.trim()) {
        newErrors[`dni_${i}`] = "DNI requerido";
      } else if (Number(p.dni) <= 0) {
        newErrors[`dni_${i}`] = "DNI debe ser mayor a 0";
      }
      if (!p.age) {
        newErrors[`age_${i}`] = "Edad requerida";
      } else if (Number(p.age) < 0) {
        newErrors[`age_${i}`] = "Edad no puede ser negativa";
      }
      if (selectedActivity?.requiresClothingSize && !p.clothingSize)
        newErrors[`size_${i}`] = "Talle requerido";
    });
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const validateParticipant = (participant) => {
    const newErrors = {};
    if (!participant.name.trim()) newErrors[`name`] = "Nombre requerido";
    if (!participant.dni.trim()) {
      newErrors[`dni`] = "DNI requerido";
    } else if (Number(participant.dni) <= 0) {
      newErrors[`dni`] = "DNI debe ser mayor a 0";
    }
    if (!participant.age) {
      newErrors[`age`] = "Edad requerida";
    } else if (Number(participant.age) < 0) {
      newErrors[`age`] = "Edad no puede ser negativa";
    }
    if (selectedActivity?.requiresClothingSize && !participant.clothingSize)
      newErrors[`size`] = "Talle requerido";
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) {
      setStatus("error");
      setErrorMessage("Corrija los errores antes de continuar.");
      return;
    }

    setIsSubmitting(true);
    setErrorMessage(""); // Limpiar errores previos
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
        setErrorMessage("No se pudo encontrar el horario seleccionado.");
        return;
      }

      const dto = {
        fechaHoraInscripcion: getLocalISOString(), // "YYYY-MM-DDTHH:mm:ss" en hora local
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

      // Guardar el DTO de respuesta para mostrar la confirmación
      setRegistrationData(resp);
      setStatus("success");
      
      // No reseteamos el formulario inmediatamente, 
      // esperamos a que el usuario confirme que vio la información
    } catch (err) {
      console.error(err);
      
      // Extraer el mensaje de error del backend
      let mensaje = "Ocurrió un error al procesar la inscripción.";
      
      if (err.response) {
        // El servidor respondió con un código de estado fuera del rango 2xx
        if (err.response.data) {
          // Intentar extraer el mensaje del error
          if (typeof err.response.data === 'string') {
            mensaje = err.response.data;
          } else if (err.response.data.message) {
            mensaje = err.response.data.message;
          } else if (err.response.data.error) {
            mensaje = err.response.data.error;
          } else if (err.response.data.mensaje) {
            mensaje = err.response.data.mensaje;
          }
        }
      } else if (err.request) {
        // La petición fue hecha pero no hubo respuesta
        mensaje = "No se pudo conectar con el servidor. Verifique su conexión.";
      } else {
        // Algo pasó al configurar la petición
        mensaje = err.message || mensaje;
      }
      
      setErrorMessage(mensaje);
      setStatus("error");
    } finally {
      setIsSubmitting(false);
    }
  };

  // Función para confirmar y resetear el formulario
  const handleConfirmRegistration = () => {
    // Reset del formulario
    setSelectedActivity(null);
    setSelectedTimeSlot("");
    setParticipants([{ name: "", dni: "", age: "", clothingSize: "" }]);
    setCurrentIndex(0);
    setNumberOfParticipants(1);
    setTermsAccepted(false);
    setProgramadas([]);
    setRegistrationData(null);
    setStatus(null);
    setErrorMessage("");
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

  // Función helper para obtener el nombre de la talla
  const getTallaName = (tallaId) => {
    if (!tallaId) return null;
    const talla = tallas.find(t => t.id === tallaId);
    return talla ? talla.nombre : `Talla ID: ${tallaId}`;
  };

  // Si hay datos de registro exitoso, mostrar la confirmación
  if (registrationData) {
    return (
      <div className="container py-4">
        <div className="card shadow">
          <div className="card-header text-center bg-success text-white">
            <h3>¡Inscripción Exitosa! ✓</h3>
            <p className="mb-0">Su reserva ha sido confirmada</p>
          </div>

          <div className="card-body">
            <div className="alert alert-success">
              <strong>Confirmación de Inscripción</strong>
              <p className="mb-0 mt-2">Por favor, revise los detalles de su inscripción:</p>
            </div>

            <div className="registration-details">
              <div className="mb-3">
                <h5 className="border-bottom pb-2">Información de la Inscripción</h5>
                <div className="row">
                  <div className="col-md-6 mb-2">
                    <strong>Fecha y Hora de Inscripción:</strong>
                    <p className="mb-0">
                      {new Date(registrationData.fechaHoraInscripcion).toLocaleString('es-AR', {
                        dateStyle: 'long',
                        timeStyle: 'short'
                      })}
                    </p>
                  </div>
                  <div className="col-md-6 mb-2">
                    <strong>Actividad:</strong>
                    <p className="mb-0">{selectedActivity?.nombre || 'N/A'}</p>
                  </div>
                  <div className="col-12 mb-2">
                    <strong>Términos y Condiciones:</strong>
                    <p className="mb-0">
                      {registrationData.aceptanTerminosYCondiciones ? '✓ Aceptados' : '✗ No aceptados'}
                    </p>
                  </div>
                </div>
              </div>

              <div className="mb-3">
                <h5 className="border-bottom pb-2">Participantes ({registrationData.participantes?.length || 0})</h5>
                {registrationData.participantes?.map((participante, index) => (
                  <div key={index} className="card mb-2">
                    <div className="card-body">
                      <h6 className="card-title">Participante {index + 1}</h6>
                      <div className="row">
                        <div className="col-md-6 mb-1">
                          <strong>Nombre:</strong> {participante.nombre}
                        </div>
                        <div className="col-md-6 mb-1">
                          <strong>DNI:</strong> {participante.dni}
                        </div>
                        <div className="col-md-6 mb-1">
                          <strong>Edad:</strong> {participante.edad} años
                        </div>
                        {participante.tallaId && (
                          <div className="col-md-6 mb-1">
                            <strong>Talla:</strong> {getTallaName(participante.tallaId)}
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              <div className="alert alert-info">
                <strong>Nota:</strong> Guarde esta información para sus registros. 
                Recibirá una confirmación adicional por correo electrónico.
              </div>
            </div>

            <button 
              onClick={handleConfirmRegistration}
              className="btn btn-primary w-100 mt-3"
            >
              Confirmar y Realizar Nueva Inscripción
            </button>
          </div>
        </div>
      </div>
    );
  }

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
            {status === "error" && errorMessage && (
              <div className="alert alert-danger" role="alert">
                <div className="d-flex align-items-start">
                  <div className="flex-shrink-0">
                    <strong>⚠️ Error:</strong>
                  </div>
                  <div className="flex-grow-1 ms-2">
                    {errorMessage}
                  </div>
                </div>
              </div>
            )}
            {status === "errorFaltanDatos" && (
              <div className="alert alert-danger">
                Complete todos los campos del participante
              </div>
            )}

            <button 
              type="submit" 
              className="btn btn-primary w-100 mt-3"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Enviando...' : 'Enviar'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

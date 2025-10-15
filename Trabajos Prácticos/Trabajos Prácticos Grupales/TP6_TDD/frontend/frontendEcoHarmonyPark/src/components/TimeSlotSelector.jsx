import React from "react";

export default function TimeSlotSelector({ slots, selected, onChange, error }) {
  const available = Object.entries(slots).filter(([_, count]) => count > 0);

  return (
    <div className="mb-3">
      <label className="form-label">Horario disponible</label>
      <select
        className={`form-select ${error ? "is-invalid" : ""}`}
        value={selected}
        onChange={(e) => onChange(e.target.value)}
      >
        <option value="">Seleccione un horario</option>
        {available.map(([time, spots]) => (
          <option key={time} value={time}>
            {time} ({spots} lugares)
          </option>
        ))}
      </select>
      {error && <div className="invalid-feedback">{error}</div>}
      {available.length === 0 && (
        <div className="text-warning small mt-1">
          No hay horarios disponibles para esta actividad
        </div>
      )}
    </div>
  );
}

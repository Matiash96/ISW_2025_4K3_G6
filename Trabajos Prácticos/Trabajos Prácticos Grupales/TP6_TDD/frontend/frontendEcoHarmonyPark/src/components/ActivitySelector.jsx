import React from "react";

export default function ActivitySelector({ activities, selectedActivity, onChange, error }) {
  return (
    <div className="mb-3">
      <label className="form-label">Actividad</label>
      <select
        className={`form-select ${error ? "is-invalid" : ""}`}
        value={selectedActivity?.id || ""}
        onChange={(e) => onChange(e.target.value)}
      >
        <option value="">Seleccione una actividad</option>
        {activities.map((a) => (
          <option key={a.id} value={a.id}>
            {a.nombre}
          </option>
        ))}
      </select>
      {error && <div className="invalid-feedback">{error}</div>}
    </div>
  );
}

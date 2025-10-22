import React from "react";

export default function ParticipantForm({
  participant,
  index,
  total,
  onChange,
  requiresClothingSize,
  next,
  prev,
  errors,
  tallas = [],
}) {
  return (
    <div className="border rounded p-3 mb-3 bg-light">
      <h5>Participante {index + 1} de {total}</h5>

      <div className="mb-2">
        <label className="form-label">Nombre</label>
        <input
          className={`form-control ${errors[`name_${index}`] ? "is-invalid" : ""}`}
          value={participant.name}
          onChange={(e) => onChange(index, "name", e.target.value)}
        />
        {errors[`name_${index}`] && (
          <div className="invalid-feedback">{errors[`name_${index}`]}</div>
        )}
      </div>

      <div className="mb-2">
        <label className="form-label">DNI</label>
        <input
          type="number"
          min="1"
          className={`form-control ${errors[`dni_${index}`] ? "is-invalid" : ""}`}
          value={participant.dni}
          onChange={(e) => {
            const value = e.target.value;
            // Solo permitir números positivos mayores a 0
            if (value === "" || (Number(value) > 0 && !value.includes('-') && !value.includes('.'))) {
              onChange(index, "dni", value);
            }
          }}
          onKeyDown={(e) => {
            // Prevenir el ingreso de caracteres no numéricos, signos negativos y puntos
            if (e.key === '-' || e.key === '.' || e.key === 'e' || e.key === 'E' || e.key === '+') {
              e.preventDefault();
            }
          }}
        />
        {errors[`dni_${index}`] && (
          <div className="invalid-feedback">{errors[`dni_${index}`]}</div>
        )}
      </div>

      <div className="mb-2">
        <label className="form-label">Edad</label>
        <input
          type="number"
          min="0"
          className={`form-control ${errors[`age_${index}`] ? "is-invalid" : ""}`}
          value={participant.age}
          onChange={(e) => {
            const value = e.target.value;
            // Solo permitir números no negativos
            if (value === "" || (Number(value) >= 0 && !value.includes('-'))) {
              onChange(index, "age", value);
            }
          }}
          onKeyDown={(e) => {
            // Prevenir el ingreso de signos negativos
            if (e.key === '-' || e.key === 'e' || e.key === 'E' || e.key === '+') {
              e.preventDefault();
            }
          }}
        />
        {errors[`age_${index}`] && (
          <div className="invalid-feedback">{errors[`age_${index}`]}</div>
        )}
      </div>

      {requiresClothingSize && (
        <div className="mb-2">
          <label className="form-label">Talle de vestimenta</label>
          <select
            className={`form-select ${errors[`size_${index}`] ? "is-invalid" : ""}`}
            value={participant.clothingSize ?? ""}         // guarda el ID de la talla
            onChange={(e) => onChange(index, "clothingSize", e.target.value)}
            disabled={!tallas.length}
          >
            <option value="">Seleccione un talle</option>
            {tallas.map((t) => (
              <option key={t.id} value={t.id}>
                {t.nombre}
              </option>
            ))}
          </select>
          {errors[`size_${index}`] && <div className="invalid-feedback">{errors[`size_${index}`]}</div>}
        </div>
      )}

      <div className="d-flex justify-content-between mt-3">
        
        <button
          type="button"
          className="btn btn-outline-secondary"
          onClick={prev}
          disabled={index === 0}
        >
          Anterior
        </button>
        
        <button
          type="button"
          className="btn btn-outline-primary"
          onClick={next}
          disabled={index === total - 1}
        >
          Siguiente
        </button>
        
      </div>
    </div>
  );
}

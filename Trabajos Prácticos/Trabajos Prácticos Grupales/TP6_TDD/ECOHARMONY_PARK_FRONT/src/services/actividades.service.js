import api from "./api";

export const getActividades = async () => {
  const { data } = await api.get("/actividades");
  return data; // List<Actividad>
};

export const getTallas = async () => {
  const { data } = await api.get("/tallas");
  return data; // List<Talla>
};

export const getProgramadas = async (idActividad) => {
  const { data } = await api.get(`/actividades-programadas/${idActividad}`);
  return data; // List<ActividadProgramada>
};

export const postInscripcion = async (inscripcionDTO) => {
  const { data } = await api.post("/inscripciones", inscripcionDTO);
  return data; // InscripcionDTO (respuesta)
};

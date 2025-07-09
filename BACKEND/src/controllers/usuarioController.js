const usuarioService = require('../services/usuarioService');

exports.getAll = async (req, res) => {
  const result = await usuarioService.getAll();
  res.json(result);
};

exports.getById = async (req, res) => {
  const result = await usuarioService.getById(req.params.id);
  if (!result) return res.status(404).send("Usuario no encontrado");
  res.json(result);
};

exports.create = async (req, res) => {
  const result = await usuarioService.create(req.body);
  res.status(201).json(result);
};

exports.update = async (req, res) => {
  const result = await usuarioService.update(req.params.id, req.body);
  res.json(result);
};

exports.delete = async (req, res) => {
  const result = await usuarioService.delete(req.params.id);
  res.json({ success: result });
};

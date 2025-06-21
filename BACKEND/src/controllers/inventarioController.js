const express = require('express');
const inventarioService = require('../services/inventarioServices');
const router = express.Router();

router.get('/', async (req, res) => {
    try {
        const inventarios = await inventarioService.getAllInventarios();
        res.json(inventarios);
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener los Inventarios', error: error.message });
    }
});

router.get('/:id', async (req, res) => {
    try {
        const iventario = await inventarioService.getInventarioById(req.params.id);
        if (iventario) {
            res.status(200).json(iventario);
        } else {
            res.status(404).json({ message: 'Inventario no encontrado' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener el Inventario', error: error.message });
    }
});

router.get('/user/:userID', async (req, res) => {
    try {
        const iventarios = await inventarioService.getInventariosByUserID(req.params.userID);
        if (iventarios) {
            res.status(200).json(iventarios);
        } else {
            res.status(404).json({ message: 'Inventarios no encontrado por UserID' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener los Inventarios del Usuario', error: error.message });
    }
});

router.post('/', async (req, res) => {
    try {
        const newInventario = await inventarioService.createInventario(req.body);
        if (newInventario) {
            res.status(201).json(newInventario);
        } else {
            res.status(404).json({ message: 'No se pudo registrar el Inventario' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al crear el Inventario', error: error.message });
    }
});

router.put('/:id', async (req, res) => {
    try {
        const updateInventario = await inventarioService.updateInventario(req.params.id, req.body);
        if (updateInventario) {
            res.status(200).json(updateInventario);
        } else {
            res.status(404).json({ message: 'Inventario no actualizado' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al actualizar el Inventario', error: error.message });
    }
});

router.delete('/:id', async (req, res) => {
    try {
        const deleteInventario = await inventarioService.deleteInventario(req.params.id);
        if (deleteInventario) {
            res.status(200).send();
        } else {
            res.status(400).json({ message: 'Inventario no eliminado' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al eliminar el Inventario', error: error.message });
    }
});

module.exports = router;
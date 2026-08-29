const fieldForm = document.getElementById('field-form');
const fieldStatus = document.getElementById('field-status');
const carSection = document.getElementById('car-section');
const carForm = document.getElementById('car-form');
const carStatus = document.getElementById('car-status');
const carsTableBody = document.querySelector('#cars-table tbody');
const runBtn = document.getElementById('run-btn');
const resetBtn = document.getElementById('reset-btn');
const resultsSection = document.getElementById('results-section');
const resultsTableBody = document.querySelector('#results-table tbody');
const runAgainBtn = document.getElementById('run-again-btn');

async function api(path, options = {}) {
    const res = await fetch(path, {
        headers: { 'Content-Type': 'application/json' },
        ...options,
    });
    const isJson = res.headers.get('content-type')?.includes('application/json');
    const body = isJson ? await res.json() : null;
    if (!res.ok) {
        throw new Error(body?.message || `Request failed (${res.status})`);
    }
    return body;
}

function showStatus(el, message, type) {
    el.textContent = message;
    el.className = `status ${type}`;
}

fieldForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const width = Number(document.getElementById('field-width').value);
    const height = Number(document.getElementById('field-height').value);
    try {
        await api('/api/field', { method: 'POST', body: JSON.stringify({ width, height }) });
        showStatus(fieldStatus, `Field created: ${width} x ${height}`, 'success');
        carSection.hidden = false;
        carsTableBody.innerHTML = '';
        runBtn.disabled = true;
        resultsSection.hidden = true;
    } catch (err) {
        showStatus(fieldStatus, err.message, 'error');
    }
});

carForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        name: document.getElementById('car-name').value.trim(),
        x: Number(document.getElementById('car-x').value),
        y: Number(document.getElementById('car-y').value),
        direction: document.getElementById('car-direction').value,
        commands: document.getElementById('car-commands').value.trim().toUpperCase(),
    };
    try {
        await api('/api/cars', { method: 'POST', body: JSON.stringify(payload) });
        showStatus(carStatus, `Car ${payload.name} added`, 'success');
        carForm.reset();
        document.getElementById('car-direction').value = 'N';
        await refreshCars();
    } catch (err) {
        showStatus(carStatus, err.message, 'error');
    }
});

async function refreshCars() {
    const cars = await api('/api/cars');
    carsTableBody.innerHTML = '';
    for (const car of cars) {
        const row = document.createElement('tr');
        row.innerHTML = `<td>${car.name}</td><td>(${car.x},${car.y}) ${car.direction}</td><td>${car.direction}</td><td>${car.commands}</td>`;
        carsTableBody.appendChild(row);
    }
    runBtn.disabled = cars.length === 0;
}

runBtn.addEventListener('click', async () => {
    try {
        const results = await api('/api/simulate', { method: 'POST' });
        resultsTableBody.innerHTML = '';
        for (const car of results) {
            const row = document.createElement('tr');
            const resultText = car.collided
                ? `<span class="collision">collides with ${car.collidedWith.join(', ')} at (${car.x},${car.y}) at step ${car.collisionStep}</span>`
                : `<span class="clean">(${car.x},${car.y}) ${car.direction}</span>`;
            row.innerHTML = `<td>${car.name}</td><td>${resultText}</td>`;
            resultsTableBody.appendChild(row);
        }
        resultsSection.hidden = false;
    } catch (err) {
        showStatus(carStatus, err.message, 'error');
    }
});

async function startOver() {
    await api('/api/reset', { method: 'POST' });
    fieldForm.reset();
    document.getElementById('field-width').value = 10;
    document.getElementById('field-height').value = 10;
    carSection.hidden = true;
    resultsSection.hidden = true;
    fieldStatus.textContent = '';
    carStatus.textContent = '';
}

resetBtn.addEventListener('click', startOver);
runAgainBtn.addEventListener('click', startOver);

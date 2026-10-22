const state = {
  tab: "normal",
  normal: [],
  priority: [],
  boarded: [],
  sequence: 1
};

const $ = (id) => document.getElementById(id);
const tabs = document.querySelectorAll(".tab");
const form = $("passengerForm");

function escapeHTML(value) {
  return String(value).replace(/[&<>"']/g, char => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  }[char]));
}
function initials(name) {
  return name.trim().split(/\s+/).slice(0, 2).map(part => part[0]).join("").toUpperCase();
}
function showToast(message) {
  const toast = $("toast");
  toast.textContent = message;
  toast.classList.add("show");
  clearTimeout(showToast.timer);
  showToast.timer = setTimeout(() => toast.classList.remove("show"), 2600);
}
function setTab(tab) {
  state.tab = tab;
  tabs.forEach(button => button.classList.toggle("active", button.dataset.tab === tab));
  $("priorityField").classList.toggle("hidden", tab !== "priority");
  $("addQueueLabel").textContent = tab === "priority" ? "priority queue" : "normal queue";
  $("passengerName").placeholder = tab === "priority" ? "e.g. Jordan Lee" : "e.g. Alex Johnson";
}
tabs.forEach(button => button.addEventListener("click", () => setTab(button.dataset.tab)));

form.addEventListener("submit", event => {
  event.preventDefault();
  const name = $("passengerName").value.trim();
  const id = $("passengerId").value.trim();
  if (!name || !id) return;
  const duplicate = [...state.normal, ...state.priority].some(p => p.id.toLowerCase() === id.toLowerCase());
  if (duplicate) {
    showToast("That passenger ID is already waiting. Use a unique ID.");
    $("passengerId").focus();
    return;
  }
  const passenger = {
    name, id,
    type: state.tab,
    priority: state.tab === "priority" ? Number($("priorityValue").value) : null,
    order: state.sequence++,
    time: new Date()
  };
  if (passenger.type === "priority") {
    state.priority.push(passenger);
    state.priority.sort((a, b) => a.priority - b.priority || a.order - b.order);
  } else {
    state.normal.push(passenger);
  }
  form.reset();
  $("priorityValue").value = "1";
  render();
  showToast(`${name} added to the ${passenger.type} queue.`);
});

function renderPassengerList(list, type) {
  if (!list.length) {
    return `<div class="empty-state">${type === "normal" ? "No passengers in the normal queue yet." : "No priority passengers waiting."}</div>`;
  }
  return list.map((p, index) => `
    <div class="passenger-row">
      <div class="passenger-avatar ${type === "priority" ? "priority" : ""}">${escapeHTML(initials(p.name))}</div>
      <div class="passenger-info"><strong>${escapeHTML(p.name)}</strong><small>${escapeHTML(p.id)}</small></div>
      ${type === "priority" ? `<span class="priority-badge">P${p.priority}</span>` : `<span class="position">#${index + 1}</span>`}
    </div>`).join("");
}
function getNextPassenger() {
  if (state.priority.length) return state.priority[0];
  if (state.normal.length) return state.normal[0];
  return null;
}
function render() {
  const normalCount = state.normal.length;
  const priorityCount = state.priority.length;
  $("totalWaiting").textContent = normalCount + priorityCount;
  $("normalCount").textContent = normalCount;
  $("priorityCount").textContent = priorityCount;
  $("boardedCount").textContent = state.boarded.length;
  $("normalQueueLabel").textContent = `${normalCount} passenger${normalCount === 1 ? "" : "s"}`;
  $("priorityQueueLabel").textContent = `${priorityCount} passenger${priorityCount === 1 ? "" : "s"}`;
  $("normalList").innerHTML = renderPassengerList(state.normal, "normal");
  $("priorityList").innerHTML = renderPassengerList(state.priority, "priority");

  const next = getNextPassenger();
  $("boardBtn").disabled = !next;
  if (next) {
    $("nextName").textContent = next.name;
    $("nextDetail").textContent = `${next.id} · ${next.type === "priority" ? `Priority ${next.priority}` : "Normal boarding"}`;
    $("nextDescription").textContent = next.type === "priority"
      ? "A priority passenger will board before the normal queue."
      : "No priority passengers are waiting. FIFO order applies.";
  } else {
    $("nextName").textContent = "Awaiting passengers";
    $("nextDetail").textContent = "No one in queue";
    $("nextDescription").textContent = "Add passengers to see who will board next.";
  }

  $("historyCount").textContent = `${state.boarded.length} record${state.boarded.length === 1 ? "" : "s"}`;
  $("boardingHistory").innerHTML = state.boarded.length
    ? state.boarded.slice().reverse().map(p => `
      <div class="history-row">
        <div class="history-check">✓</div>
        <div><strong>${escapeHTML(p.name)}</strong><small>${escapeHTML(p.id)} · ${p.type === "priority" ? `Priority ${p.priority}` : "Normal queue"}</small></div>
        <span class="history-time">${p.boardedAt.toLocaleTimeString([], {hour:"2-digit", minute:"2-digit"})}</span>
      </div>`).join("")
    : `<div class="empty-state">Boarded passengers will appear here.</div>`;
}
$("boardBtn").addEventListener("click", () => {
  const passenger = state.priority.length ? state.priority.shift() : state.normal.shift();
  if (!passenger) return;
  passenger.boardedAt = new Date();
  state.boarded.push(passenger);
  render();
  showToast(`${passenger.name} has boarded successfully.`);
});
$("resetBtn").addEventListener("click", () => {
  state.normal = [];
  state.priority = [];
  state.boarded = [];
  state.sequence = 1;
  render();
  showToast("Demo reset. All queues are clear.");
});
function updateClock() {
  $("clock").textContent = new Date().toLocaleTimeString([], {hour:"2-digit", minute:"2-digit", second:"2-digit", hour12:false});
}
setInterval(updateClock, 1000);
updateClock();
render();

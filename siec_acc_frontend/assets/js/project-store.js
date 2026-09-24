/**
 * project-store.js
 * Temporary frontend-only data layer for the QR Generator flow.
 *
 * WHY THIS FILE EXISTS:
 * qr-generator.html (save) and project-info.html (read) both need to
 * save/read the same project record. Right now it's backed by
 * localStorage so the whole flow works end-to-end without a backend.
 *
 * WHEN YOU ADD THE REAL BACKEND API:
 * Only this file changes. Replace the body of save()/get() with your
 * REST calls (e.g. POST /api/projects, GET /api/projects/:id) and
 * return the same shaped objects — qr-generator.html and
 * project-info.html don't need to change at all, since they only call
 * ProjectStore.save() / ProjectStore.get() / ProjectStore.upsert().
 *
 * DATA SHAPE (keep this shape when you wire the real API):
 * {
 *   id: "proj_xxx",                 // used in the QR URL: project-info.html?id=proj_xxx
 *   projectName: "string",
 *   productOverview: "string",
 *   vendorDetails: "string",
 *   notes: "string",
 *   image: { name, type, dataUrl } | null,
 *   completionReport: { name, type, dataUrl } | null,
 *   generalReport: { name, type, dataUrl } | null,
 *   createdAt: 1234567890,
 *   updatedAt: 1234567890
 * }
 *
 * IMPORTANT LIMITATION (frontend-only phase):
 * localStorage is per-browser, per-device. Scanning the QR on a
 * different phone/device will NOT find the record until this is
 * wired to a real backend — that's expected at this stage.
 */

(function (window) {
  "use strict";

  var STORAGE_PREFIX = "projectStore:";

  function genId() {
    if (window.crypto && window.crypto.randomUUID) {
      return "proj_" + window.crypto.randomUUID();
    }
    return "proj_" + Date.now().toString(36) + Math.random().toString(36).slice(2, 8);
  }

  function fileToRecord(file) {
    return new Promise(function (resolve, reject) {
      if (!file) return resolve(null);
      var reader = new FileReader();
      reader.onload = function () {
        resolve({ name: file.name, type: file.type, dataUrl: reader.result });
      };
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  }

  var ProjectStore = {
    genId: genId,
    fileToRecord: fileToRecord,

    /**
     * Create or update a project record.
     * Pass `id` to update an existing record (keeps the same QR/link).
     * Omit `id` to create a new one.
     */
    upsert: function (data, id) {
      var now = Date.now();
      var existing = id ? this.get(id) : null;
      var record = Object.assign(
        {
          id: id || genId(),
          createdAt: existing ? existing.createdAt : now,
        },
        data,
        { updatedAt: now }
      );
      // TODO(backend): replace with e.g.
      //   return fetch("/api/projects" + (id ? "/" + id : ""), {
      //     method: id ? "PUT" : "POST",
      //     headers: { "Content-Type": "application/json" },
      //     body: JSON.stringify(data)
      //   }).then(res => res.json());
      window.localStorage.setItem(STORAGE_PREFIX + record.id, JSON.stringify(record));
      return Promise.resolve(record);
    },

    get: function (id) {
      // TODO(backend): replace with e.g.
      //   return fetch("/api/projects/" + id).then(res => res.ok ? res.json() : null);
      var raw = window.localStorage.getItem(STORAGE_PREFIX + id);
      return raw ? JSON.parse(raw) : null;
    },

    /**
     * Returns all project records, newest first.
     */
    list: function () {
      // TODO(backend): replace with e.g.
      //   return fetch("/api/projects").then(res => res.json());
      var records = [];
      for (var i = 0; i < window.localStorage.length; i++) {
        var key = window.localStorage.key(i);
        if (key.indexOf(STORAGE_PREFIX) === 0) {
          try {
            records.push(JSON.parse(window.localStorage.getItem(key)));
          } catch (e) {
            /* skip corrupt entry */
          }
        }
      }
      records.sort(function (a, b) { return (b.createdAt || 0) - (a.createdAt || 0); });
      return records;
    },

    /**
     * Deletes a project record permanently.
     */
    remove: function (id) {
      // TODO(backend): replace with e.g.
      //   return fetch("/api/projects/" + id, { method: "DELETE" });
      window.localStorage.removeItem(STORAGE_PREFIX + id);
      return Promise.resolve(true);
    },
  };

  window.ProjectStore = ProjectStore;
})(window);
export function Songs() {
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold text-slate-100">As Minhas Músicas</h2>
        <button className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium rounded-lg transition">
          + Nova Música
        </button>
      </div>
      <div className="p-8 border border-dashed border-slate-700 rounded-xl text-center">
        <p className="text-slate-400">Ainda não tens nenhuma música registada.</p>
      </div>
    </div>
  );
}
export function getDisplaySummary(q) {
  const base = q.summary && q.summary.length <= 50 ? q.summary : q.content || q.question || q.description || '';
  if (base.length <= 50) return base;
  return `${base.slice(0, 47)}...`;
}

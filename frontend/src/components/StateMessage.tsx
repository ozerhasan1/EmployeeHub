interface StateMessageProps {
  title: string;
  message?: string;
}

export function StateMessage({ title, message }: StateMessageProps) {
  return (
    <div className="rounded border border-slate-200 bg-white px-4 py-5 text-sm">
      <p className="font-medium text-slate-800">{title}</p>
      {message ? <p className="mt-1 text-slate-500">{message}</p> : null}
    </div>
  );
}

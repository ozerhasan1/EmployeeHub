import { FormEvent, useCallback, useEffect, useMemo, useState } from 'react';
import { createDepartment, deleteDepartment, getDepartments, updateDepartment } from './api/departmentApi';
import { getDashboardSummary } from './api/dashboardApi';
import {
  createEmployee,
  getEmployee,
  getEmployees,
  terminateEmployee,
  updateEmployee,
} from './api/employeeApi';
import { getApiErrorMessage } from './api/axiosClient';
import { StateMessage } from './components/StateMessage';
import { StatusBadge } from './components/StatusBadge';
import type { DashboardSummary } from './types/dashboard';
import type { Department, DepartmentFormData } from './types/department';
import type { Employee, EmployeeFormData, EmployeeListParams, EmployeeSummary, EmploymentStatus } from './types/employee';
import type { PageResponse } from './types/api';

type Page = 'dashboard' | 'employees' | 'departments';

const emptyDepartmentForm: DepartmentFormData = {
  name: '',
  description: '',
};

const emptyEmployeeForm: EmployeeFormData = {
  firstName: '',
  lastName: '',
  email: '',
  phoneNumber: '',
  jobTitle: '',
  employmentStatus: 'ACTIVE',
  hireDate: new Date().toISOString().slice(0, 10),
  salary: '',
  departmentId: '',
};

function App() {
  const [activePage, setActivePage] = useState<Page>('dashboard');
  const [departments, setDepartments] = useState<Department[]>([]);
  const [dashboard, setDashboard] = useState<DashboardSummary | null>(null);
  const [employees, setEmployees] = useState<PageResponse<EmployeeSummary> | null>(null);
  const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);
  const [employeeForm, setEmployeeForm] = useState<EmployeeFormData>(emptyEmployeeForm);
  const [departmentForm, setDepartmentForm] = useState<DepartmentFormData>(emptyDepartmentForm);
  const [editingEmployeeId, setEditingEmployeeId] = useState<number | null>(null);
  const [editingDepartmentId, setEditingDepartmentId] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [filters, setFilters] = useState<EmployeeListParams>({
    page: 0,
    size: 10,
    sortBy: 'createdAt',
    sortDirection: 'desc',
    search: '',
    departmentId: '',
    status: '',
  });

  const loadDepartments = useCallback(async () => {
    const data = await getDepartments();
    setDepartments(data);
    if (!employeeForm.departmentId && data.length > 0) {
      setEmployeeForm((current) => ({ ...current, departmentId: String(data[0].id) }));
    }
  }, [employeeForm.departmentId]);

  const loadDashboard = useCallback(async () => {
    const data = await getDashboardSummary();
    setDashboard(data);
  }, []);

  const loadEmployees = useCallback(async () => {
    const data = await getEmployees({
      ...filters,
      search: filters.search || undefined,
      departmentId: filters.departmentId || undefined,
      status: filters.status || undefined,
    });
    setEmployees(data);
  }, [filters]);

  const refreshCurrentPage = useCallback(async () => {
    setError(null);
    setLoading(true);
    try {
      await loadDepartments();
      if (activePage === 'dashboard') {
        await loadDashboard();
      }
      if (activePage === 'employees') {
        await loadEmployees();
      }
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [activePage, loadDashboard, loadDepartments, loadEmployees]);

  useEffect(() => {
    refreshCurrentPage();
  }, [refreshCurrentPage]);

  const departmentOptions = useMemo(
    () => departments.map((department) => ({ label: department.name, value: String(department.id) })),
    [departments],
  );

  async function handleEmployeeSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    setNotice(null);
    try {
      if (editingEmployeeId) {
        await updateEmployee(editingEmployeeId, employeeForm);
        setNotice('Employee updated.');
      } else {
        await createEmployee(employeeForm);
        setNotice('Employee created.');
      }
      resetEmployeeForm();
      await loadEmployees();
      await loadDashboard();
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  async function handleDepartmentSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    setNotice(null);
    try {
      if (editingDepartmentId) {
        await updateDepartment(editingDepartmentId, departmentForm);
        setNotice('Department updated.');
      } else {
        await createDepartment(departmentForm);
        setNotice('Department created.');
      }
      resetDepartmentForm();
      await loadDepartments();
      await loadDashboard();
      await loadEmployees();
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  async function handleEmployeeSelect(id: number) {
    setError(null);
    try {
      const employee = await getEmployee(id);
      setSelectedEmployee(employee);
    } catch (err) {
      setError(getApiErrorMessage(err));
    }
  }

  async function handleEmployeeTerminate(id: number) {
    setSaving(true);
    setError(null);
    setNotice(null);
    try {
      await terminateEmployee(id);
      setNotice('Employee terminated.');
      await loadEmployees();
      await loadDashboard();
      if (selectedEmployee?.id === id) {
        const employee = await getEmployee(id);
        setSelectedEmployee(employee);
      }
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  async function handleDepartmentDelete(id: number) {
    setSaving(true);
    setError(null);
    setNotice(null);
    try {
      await deleteDepartment(id);
      setNotice('Department deleted.');
      await loadDepartments();
      await loadDashboard();
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  function startEmployeeEdit(employee: EmployeeSummary | Employee) {
    setEditingEmployeeId(employee.id);
    setEmployeeForm({
      firstName: employee.firstName,
      lastName: employee.lastName,
      email: employee.email,
      phoneNumber: 'phoneNumber' in employee ? employee.phoneNumber ?? '' : '',
      jobTitle: employee.jobTitle,
      employmentStatus: employee.employmentStatus,
      hireDate: 'hireDate' in employee ? employee.hireDate : new Date().toISOString().slice(0, 10),
      salary: 'salary' in employee && employee.salary ? String(employee.salary) : '',
      departmentId: String(employee.department.id),
    });
    setActivePage('employees');
  }

  function startDepartmentEdit(department: Department) {
    setEditingDepartmentId(department.id);
    setDepartmentForm({
      name: department.name,
      description: department.description ?? '',
    });
  }

  function resetEmployeeForm() {
    setEditingEmployeeId(null);
    setEmployeeForm({
      ...emptyEmployeeForm,
      departmentId: departments[0] ? String(departments[0].id) : '',
    });
  }

  function resetDepartmentForm() {
    setEditingDepartmentId(null);
    setDepartmentForm(emptyDepartmentForm);
  }

  return (
    <div className="min-h-screen bg-slate-100">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-6 lg:px-8">
          <div>
            <p className="text-sm font-medium text-slate-500">EmployeeHub</p>
            <h1 className="text-2xl font-semibold text-slate-900">Workforce Management</h1>
          </div>
          <nav className="flex rounded border border-slate-200 bg-slate-50 p-1">
            {(['dashboard', 'employees', 'departments'] as Page[]).map((page) => (
              <button
                key={page}
                className={`rounded px-3 py-2 text-sm font-medium capitalize ${
                  activePage === page ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-600 hover:text-slate-900'
                }`}
                onClick={() => {
                  setActivePage(page);
                  setError(null);
                  setNotice(null);
                }}
                type="button"
              >
                {page}
              </button>
            ))}
          </nav>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
        {error ? <StateMessage title="Something went wrong" message={error} /> : null}
        {notice ? <div className="mb-4 rounded border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800">{notice}</div> : null}
        {loading ? <StateMessage title="Loading data" message="Fetching the latest records from EmployeeHub." /> : null}

        {!loading && activePage === 'dashboard' && dashboard ? (
          <DashboardPage dashboard={dashboard} onOpenEmployees={() => setActivePage('employees')} />
        ) : null}

        {!loading && activePage === 'employees' ? (
          <EmployeesPage
            employees={employees}
            departments={departments}
            filters={filters}
            employeeForm={employeeForm}
            editingEmployeeId={editingEmployeeId}
            selectedEmployee={selectedEmployee}
            saving={saving}
            departmentOptions={departmentOptions}
            onFilterChange={setFilters}
            onFormChange={setEmployeeForm}
            onSubmit={handleEmployeeSubmit}
            onReset={resetEmployeeForm}
            onSelect={handleEmployeeSelect}
            onEdit={startEmployeeEdit}
            onTerminate={handleEmployeeTerminate}
          />
        ) : null}

        {!loading && activePage === 'departments' ? (
          <DepartmentsPage
            departments={departments}
            departmentForm={departmentForm}
            editingDepartmentId={editingDepartmentId}
            saving={saving}
            onFormChange={setDepartmentForm}
            onSubmit={handleDepartmentSubmit}
            onReset={resetDepartmentForm}
            onEdit={startDepartmentEdit}
            onDelete={handleDepartmentDelete}
          />
        ) : null}
      </main>
    </div>
  );
}

interface DashboardPageProps {
  dashboard: DashboardSummary;
  onOpenEmployees: () => void;
}

function DashboardPage({ dashboard, onOpenEmployees }: DashboardPageProps) {
  const metrics = [
    ['Total employees', dashboard.totalEmployees],
    ['Active', dashboard.activeEmployees],
    ['On leave', dashboard.onLeaveEmployees],
    ['Terminated', dashboard.terminatedEmployees],
    ['Departments', dashboard.totalDepartments],
  ];

  return (
    <div className="space-y-6">
      <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
        {metrics.map(([label, value]) => (
          <div key={label} className="rounded border border-slate-200 bg-white p-4">
            <p className="text-sm font-medium text-slate-500">{label}</p>
            <p className="mt-2 text-3xl font-semibold text-slate-900">{value}</p>
          </div>
        ))}
      </section>

      <section className="grid gap-6 lg:grid-cols-2">
        <div className="rounded border border-slate-200 bg-white">
          <div className="border-b border-slate-200 px-4 py-3">
            <h2 className="text-base font-semibold text-slate-900">Employees By Department</h2>
          </div>
          <div className="divide-y divide-slate-100">
            {dashboard.employeesByDepartment.map((item) => (
              <div key={item.departmentId} className="flex items-center justify-between px-4 py-3">
                <span className="text-sm font-medium text-slate-700">{item.departmentName}</span>
                <span className="text-sm text-slate-500">{item.employeeCount}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="rounded border border-slate-200 bg-white">
          <div className="flex items-center justify-between border-b border-slate-200 px-4 py-3">
            <h2 className="text-base font-semibold text-slate-900">Recent Hires</h2>
            <button className="text-sm font-medium text-slate-700 hover:text-slate-950" onClick={onOpenEmployees} type="button">
              View employees
            </button>
          </div>
          <div className="divide-y divide-slate-100">
            {dashboard.recentHires.map((hire) => (
              <div key={hire.id} className="px-4 py-3">
                <div className="flex items-center justify-between gap-3">
                  <p className="text-sm font-medium text-slate-800">
                    {hire.firstName} {hire.lastName}
                  </p>
                  <p className="text-xs text-slate-500">{formatDate(hire.hireDate)}</p>
                </div>
                <p className="mt-1 text-sm text-slate-500">
                  {hire.jobTitle} · {hire.departmentName}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}

interface EmployeesPageProps {
  employees: PageResponse<EmployeeSummary> | null;
  departments: Department[];
  filters: EmployeeListParams;
  employeeForm: EmployeeFormData;
  editingEmployeeId: number | null;
  selectedEmployee: Employee | null;
  saving: boolean;
  departmentOptions: Array<{ label: string; value: string }>;
  onFilterChange: (filters: EmployeeListParams) => void;
  onFormChange: (form: EmployeeFormData) => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  onReset: () => void;
  onSelect: (id: number) => void;
  onEdit: (employee: EmployeeSummary | Employee) => void;
  onTerminate: (id: number) => void;
}

function EmployeesPage(props: EmployeesPageProps) {
  const {
    employees,
    departments,
    filters,
    employeeForm,
    editingEmployeeId,
    selectedEmployee,
    saving,
    departmentOptions,
    onFilterChange,
    onFormChange,
    onSubmit,
    onReset,
    onSelect,
    onEdit,
    onTerminate,
  } = props;

  return (
    <div className="grid gap-6 xl:grid-cols-[1fr_380px]">
      <section className="space-y-4">
        <div className="rounded border border-slate-200 bg-white p-4">
          <div className="grid gap-3 md:grid-cols-[1fr_180px_160px]">
            <input
              className="rounded border border-slate-300 px-3 py-2 text-sm"
              placeholder="Search employees"
              value={filters.search ?? ''}
              onChange={(event) => onFilterChange({ ...filters, search: event.target.value, page: 0 })}
            />
            <select
              className="rounded border border-slate-300 px-3 py-2 text-sm"
              value={filters.departmentId ?? ''}
              onChange={(event) => onFilterChange({ ...filters, departmentId: event.target.value, page: 0 })}
            >
              <option value="">All departments</option>
              {departments.map((department) => (
                <option key={department.id} value={department.id}>
                  {department.name}
                </option>
              ))}
            </select>
            <select
              className="rounded border border-slate-300 px-3 py-2 text-sm"
              value={filters.status ?? ''}
              onChange={(event) => onFilterChange({ ...filters, status: event.target.value as EmploymentStatus | '', page: 0 })}
            >
              <option value="">All statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="ON_LEAVE">On leave</option>
              <option value="TERMINATED">Terminated</option>
            </select>
          </div>
        </div>

        <div className="overflow-hidden rounded border border-slate-200 bg-white">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-slate-50">
              <tr>
                <TableHead label="Employee" />
                <TableHead label="Role" />
                <TableHead label="Department" />
                <TableHead label="Status" />
                <TableHead label="" />
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {employees?.content.map((employee) => (
                <tr key={employee.id} className="hover:bg-slate-50">
                  <td className="px-4 py-3">
                    <button className="text-left" onClick={() => onSelect(employee.id)} type="button">
                      <span className="block text-sm font-medium text-slate-900">
                        {employee.firstName} {employee.lastName}
                      </span>
                      <span className="block text-sm text-slate-500">{employee.email}</span>
                    </button>
                  </td>
                  <td className="px-4 py-3 text-sm text-slate-600">{employee.jobTitle}</td>
                  <td className="px-4 py-3 text-sm text-slate-600">{employee.department.name}</td>
                  <td className="px-4 py-3"><StatusBadge status={employee.employmentStatus} /></td>
                  <td className="px-4 py-3 text-right">
                    <button className="text-sm font-medium text-slate-700 hover:text-slate-950" onClick={() => onEdit(employee)} type="button">
                      Edit
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {employees?.content.length === 0 ? <div className="px-4 py-8 text-sm text-slate-500">No employees found.</div> : null}
          <div className="flex items-center justify-between border-t border-slate-200 px-4 py-3 text-sm text-slate-600">
            <span>
              Page {(employees?.page ?? 0) + 1} of {employees?.totalPages || 1}
            </span>
            <div className="flex gap-2">
              <button
                className="rounded border border-slate-300 px-3 py-1 disabled:opacity-40"
                disabled={employees?.first ?? true}
                onClick={() => onFilterChange({ ...filters, page: Math.max((filters.page ?? 0) - 1, 0) })}
                type="button"
              >
                Previous
              </button>
              <button
                className="rounded border border-slate-300 px-3 py-1 disabled:opacity-40"
                disabled={employees?.last ?? true}
                onClick={() => onFilterChange({ ...filters, page: (filters.page ?? 0) + 1 })}
                type="button"
              >
                Next
              </button>
            </div>
          </div>
        </div>
      </section>

      <aside className="space-y-4">
        <EmployeeForm
          form={employeeForm}
          departmentOptions={departmentOptions}
          editingEmployeeId={editingEmployeeId}
          saving={saving}
          onChange={onFormChange}
          onReset={onReset}
          onSubmit={onSubmit}
        />
        {selectedEmployee ? (
          <EmployeeDetails employee={selectedEmployee} onEdit={onEdit} onTerminate={onTerminate} saving={saving} />
        ) : null}
      </aside>
    </div>
  );
}

function EmployeeForm({
  form,
  departmentOptions,
  editingEmployeeId,
  saving,
  onChange,
  onReset,
  onSubmit,
}: {
  form: EmployeeFormData;
  departmentOptions: Array<{ label: string; value: string }>;
  editingEmployeeId: number | null;
  saving: boolean;
  onChange: (form: EmployeeFormData) => void;
  onReset: () => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
}) {
  return (
    <form className="rounded border border-slate-200 bg-white p-4" onSubmit={onSubmit}>
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-base font-semibold text-slate-900">{editingEmployeeId ? 'Edit Employee' : 'Create Employee'}</h2>
        {editingEmployeeId ? (
          <button className="text-sm text-slate-500 hover:text-slate-800" onClick={onReset} type="button">
            Cancel
          </button>
        ) : null}
      </div>
      <div className="grid gap-3">
        <Input label="First name" value={form.firstName} onChange={(value) => onChange({ ...form, firstName: value })} required />
        <Input label="Last name" value={form.lastName} onChange={(value) => onChange({ ...form, lastName: value })} required />
        <Input label="Email" type="email" value={form.email} onChange={(value) => onChange({ ...form, email: value })} required />
        <Input label="Phone" value={form.phoneNumber} onChange={(value) => onChange({ ...form, phoneNumber: value })} />
        <Input label="Job title" value={form.jobTitle} onChange={(value) => onChange({ ...form, jobTitle: value })} required />
        <Select
          label="Department"
          value={form.departmentId}
          options={departmentOptions}
          onChange={(value) => onChange({ ...form, departmentId: value })}
          required
        />
        <Select
          label="Status"
          value={form.employmentStatus}
          options={[
            { label: 'Active', value: 'ACTIVE' },
            { label: 'On leave', value: 'ON_LEAVE' },
            { label: 'Terminated', value: 'TERMINATED' },
          ]}
          onChange={(value) => onChange({ ...form, employmentStatus: value as EmploymentStatus })}
          required
        />
        <Input label="Hire date" type="date" value={form.hireDate} onChange={(value) => onChange({ ...form, hireDate: value })} required />
        <Input label="Salary" type="number" value={form.salary} onChange={(value) => onChange({ ...form, salary: value })} />
      </div>
      <button
        className="mt-4 w-full rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50"
        disabled={saving}
        type="submit"
      >
        {saving ? 'Saving...' : editingEmployeeId ? 'Save employee' : 'Create employee'}
      </button>
    </form>
  );
}

function EmployeeDetails({
  employee,
  onEdit,
  onTerminate,
  saving,
}: {
  employee: Employee;
  onEdit: (employee: Employee) => void;
  onTerminate: (id: number) => void;
  saving: boolean;
}) {
  return (
    <div className="rounded border border-slate-200 bg-white p-4">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h2 className="text-base font-semibold text-slate-900">
            {employee.firstName} {employee.lastName}
          </h2>
          <p className="mt-1 text-sm text-slate-500">{employee.email}</p>
        </div>
        <StatusBadge status={employee.employmentStatus} />
      </div>
      <dl className="mt-4 grid gap-3 text-sm">
        <Detail label="Job title" value={employee.jobTitle} />
        <Detail label="Department" value={employee.department.name} />
        <Detail label="Phone" value={employee.phoneNumber || 'Not provided'} />
        <Detail label="Hire date" value={formatDate(employee.hireDate)} />
        <Detail label="Salary" value={employee.salary ? formatCurrency(employee.salary) : 'Not provided'} />
      </dl>
      <div className="mt-4 flex gap-2">
        <button className="rounded border border-slate-300 px-3 py-2 text-sm font-medium" onClick={() => onEdit(employee)} type="button">
          Edit
        </button>
        <button
          className="rounded border border-slate-300 px-3 py-2 text-sm font-medium disabled:opacity-50"
          disabled={saving || employee.employmentStatus === 'TERMINATED'}
          onClick={() => onTerminate(employee.id)}
          type="button"
        >
          Terminate
        </button>
      </div>
    </div>
  );
}

interface DepartmentsPageProps {
  departments: Department[];
  departmentForm: DepartmentFormData;
  editingDepartmentId: number | null;
  saving: boolean;
  onFormChange: (form: DepartmentFormData) => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  onReset: () => void;
  onEdit: (department: Department) => void;
  onDelete: (id: number) => void;
}

function DepartmentsPage({
  departments,
  departmentForm,
  editingDepartmentId,
  saving,
  onFormChange,
  onSubmit,
  onReset,
  onEdit,
  onDelete,
}: DepartmentsPageProps) {
  return (
    <div className="grid gap-6 lg:grid-cols-[1fr_360px]">
      <div className="overflow-hidden rounded border border-slate-200 bg-white">
        <table className="min-w-full divide-y divide-slate-200">
          <thead className="bg-slate-50">
            <tr>
              <TableHead label="Department" />
              <TableHead label="Employees" />
              <TableHead label="" />
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {departments.map((department) => (
              <tr key={department.id}>
                <td className="px-4 py-3">
                  <p className="text-sm font-medium text-slate-900">{department.name}</p>
                  <p className="mt-1 text-sm text-slate-500">{department.description || 'No description'}</p>
                </td>
                <td className="px-4 py-3 text-sm text-slate-600">{department.employeeCount}</td>
                <td className="px-4 py-3 text-right">
                  <div className="flex justify-end gap-3">
                    <button className="text-sm font-medium text-slate-700 hover:text-slate-950" onClick={() => onEdit(department)} type="button">
                      Edit
                    </button>
                    <button
                      className="text-sm font-medium text-slate-500 hover:text-slate-900 disabled:opacity-40"
                      disabled={saving || department.employeeCount > 0}
                      onClick={() => onDelete(department.id)}
                      type="button"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <form className="rounded border border-slate-200 bg-white p-4" onSubmit={onSubmit}>
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-base font-semibold text-slate-900">{editingDepartmentId ? 'Edit Department' : 'Create Department'}</h2>
          {editingDepartmentId ? (
            <button className="text-sm text-slate-500 hover:text-slate-800" onClick={onReset} type="button">
              Cancel
            </button>
          ) : null}
        </div>
        <div className="grid gap-3">
          <Input label="Name" value={departmentForm.name} onChange={(value) => onFormChange({ ...departmentForm, name: value })} required />
          <label className="grid gap-1 text-sm">
            <span className="font-medium text-slate-700">Description</span>
            <textarea
              className="min-h-24 rounded border border-slate-300 px-3 py-2"
              value={departmentForm.description}
              onChange={(event) => onFormChange({ ...departmentForm, description: event.target.value })}
            />
          </label>
        </div>
        <button
          className="mt-4 w-full rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50"
          disabled={saving}
          type="submit"
        >
          {saving ? 'Saving...' : editingDepartmentId ? 'Save department' : 'Create department'}
        </button>
      </form>
    </div>
  );
}

function Input({
  label,
  value,
  onChange,
  type = 'text',
  required = false,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
  required?: boolean;
}) {
  return (
    <label className="grid gap-1 text-sm">
      <span className="font-medium text-slate-700">{label}</span>
      <input
        className="rounded border border-slate-300 px-3 py-2"
        required={required}
        type={type}
        value={value}
        onChange={(event) => onChange(event.target.value)}
      />
    </label>
  );
}

function Select({
  label,
  value,
  options,
  onChange,
  required = false,
}: {
  label: string;
  value: string;
  options: Array<{ label: string; value: string }>;
  onChange: (value: string) => void;
  required?: boolean;
}) {
  return (
    <label className="grid gap-1 text-sm">
      <span className="font-medium text-slate-700">{label}</span>
      <select className="rounded border border-slate-300 px-3 py-2" required={required} value={value} onChange={(event) => onChange(event.target.value)}>
        <option value="">Select</option>
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </label>
  );
}

function TableHead({ label }: { label: string }) {
  return <th className="px-4 py-3 text-left text-xs font-semibold uppercase text-slate-500">{label}</th>;
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt className="text-xs font-medium uppercase text-slate-500">{label}</dt>
      <dd className="mt-1 text-slate-800">{value}</dd>
    </div>
  );
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat(undefined, { year: 'numeric', month: 'short', day: 'numeric' }).format(new Date(`${value}T00:00:00`));
}

function formatCurrency(value: number) {
  return new Intl.NumberFormat(undefined, { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(value);
}

export default App;

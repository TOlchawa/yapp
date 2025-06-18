import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import Logs from './Logs.jsx';

describe('Logs view', () => {
  it('shows title and back buttons', () => {
    render(<Logs />);
    expect(screen.getByRole('heading', { name: 'Logs' })).toBeInTheDocument();
    expect(screen.getAllByRole('button', { name: 'Back' })).toHaveLength(2);
    expect(screen.getByRole('textbox')).toBeInTheDocument();
  });
});

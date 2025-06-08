import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import AddView from './AddView.jsx';

describe('AddView', () => {
  it('shows Add title', () => {
    render(<AddView />);
    expect(screen.getByRole('heading', { name: 'Add' })).toBeInTheDocument();
  });
});

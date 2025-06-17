import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import Compare from './Compare.jsx';

describe('Compare', () => {
  it('shows Compare title', () => {
    render(<Compare />);
    expect(
      screen.getByRole('heading', { name: 'Compare' })
    ).toBeInTheDocument();
    expect(screen.getAllByRole('button', { name: 'Back' })).toHaveLength(2);
  });
});

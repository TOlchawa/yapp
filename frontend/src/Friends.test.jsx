import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import Friends from './Friends.jsx';

describe('Friends component', () => {
  it('shows title', () => {
    render(<Friends />);
    expect(
      screen.getByRole('heading', { level: 1, name: 'Friends' })
    ).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Back' })).toBeInTheDocument();
  });
});

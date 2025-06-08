import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import Questions from './Questions.jsx';

describe('Questions view', () => {
  it('shows Questions title', () => {
    render(<Questions />);
    expect(screen.getByRole('heading', { name: 'Questions' })).toBeInTheDocument();
  });
});

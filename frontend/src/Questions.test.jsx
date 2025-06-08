import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import Questions from './Questions.jsx';

describe('Questions view', () => {
  it('shows Questions title', () => {
    render(<Questions />);
    expect(screen.getByRole('heading', { name: 'Questions' })).toBeInTheDocument();
  });

  it('renders truncated questions list', () => {
    window.innerWidth = 80;
    render(<Questions />);
    const items = screen.getAllByRole('listitem');
    expect(items).toHaveLength(3);
    expect(items.some((li) => li.textContent.endsWith('...'))).toBe(true);
  });
});

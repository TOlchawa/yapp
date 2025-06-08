import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import Ask from './Ask.jsx';

describe('Ask view', () => {
  it('shows the Ask title', () => {
    render(<Ask />);
    expect(screen.getByRole('heading', { name: 'Ask' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Back' })).toBeInTheDocument();
  });

  it('shows a textarea and Ask button', () => {
    render(<Ask />);
    expect(screen.getByTestId('ask-textarea')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Ask' })).toBeInTheDocument();
  });
});

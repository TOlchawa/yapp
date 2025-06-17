import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import ItemDetails from './ItemDetails.jsx';

const sampleItem = {
  name: 'Beer',
  description: {
    barcode: '123',
    note: 'note',
    pictures: [{ picture: 'imgdata' }],
  },
};

describe('ItemDetails view', () => {
  it('shows title and item info', () => {
    render(<ItemDetails item={sampleItem} />);
    expect(screen.getByRole('heading', { name: 'Item details' })).toBeInTheDocument();
    expect(screen.getByText('Beer')).toBeInTheDocument();
    expect(screen.getByText('Barcode: 123')).toBeInTheDocument();
    expect(screen.getByAltText('Item')).toBeInTheDocument();
  });
});

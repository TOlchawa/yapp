import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import FriendDetails from './FriendDetails.jsx';

describe('FriendDetails', () => {
  it('shows friend info', () => {
    const relation = { id: 'r1', friendId: 'f1', type: 'FRIENDS' };
    render(<FriendDetails relation={relation} />);
    expect(screen.getByText('Friend relation')).toBeInTheDocument();
    expect(screen.getByText('Friend ID: f1')).toBeInTheDocument();
    expect(screen.getByText('Type: FRIENDS')).toBeInTheDocument();
  });
});

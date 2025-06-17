import React from 'react';

export default function ItemDetails({ item, onBack = () => {} }) {
  if (!item) {
    return null;
  }
  const { name, description } = item;
  const barcode = description && description.barcode;
  const note = description && description.note;
  const picture = description && description.pictures && description.pictures[0];
  let imgSrc = null;
  if (picture && picture.picture) {
    const data = picture.picture;
    if (data.startsWith('data:')) {
      imgSrc = data;
    } else {
      imgSrc = `data:image/jpeg;base64,${data}`;
    }
  }
  return (
    <div>
      <div className="view-header">
        <h1>Item details</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <h2>{name}</h2>
      {barcode && <p>Barcode: {barcode}</p>}
      {note && <p>{note}</p>}
      {imgSrc && <img src={imgSrc} alt="Item" />}
    </div>
  );
}

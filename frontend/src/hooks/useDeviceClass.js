import { useEffect } from 'react';

export default function useDeviceClass() {
  useEffect(() => {
    function updateClass() {
      if (typeof window === 'undefined') return;
      const isMobile =
        /Mobi|Android/i.test(navigator.userAgent) || window.innerWidth <= 600;
      document.body.classList.toggle('mobile', isMobile);
      document.body.classList.toggle('desktop', !isMobile);
    }
    updateClass();
    window.addEventListener('resize', updateClass);
    return () => window.removeEventListener('resize', updateClass);
  }, []);
}

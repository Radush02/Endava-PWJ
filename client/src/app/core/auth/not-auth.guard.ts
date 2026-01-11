import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const notAuthGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.loggedIn().toPromise().then(
    () => {
      router.navigate(['/home']);
      return false;
    }
  ).catch(
    () => {
      return true;
    }
  );
};

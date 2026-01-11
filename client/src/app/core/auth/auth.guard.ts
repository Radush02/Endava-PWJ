import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const AuthGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.loggedIn().toPromise().then(
    () => {
      return true;
    }
  ).catch(
    () => {
      router.navigate(['/login']);
      return false;
    }
  );
};

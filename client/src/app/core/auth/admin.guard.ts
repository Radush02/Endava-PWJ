import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserService } from '../user/user.service';
import { UserDTO } from '../models/user.request';
import { Role } from '../models/login.request';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';

export const AdminGuard: CanActivateFn = (route, state) => {
  const userService = inject(UserService);
  const router = inject(Router);

return firstValueFrom(userService.me())
  .then((res: UserDTO) => {
    if (res.role === Role.ADMIN) return true;
    router.navigate(['/home']);
    return false;
  })
  .catch(() => {
    router.navigate(['/home']);
    return false;
  });


};

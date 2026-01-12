import { Component } from '@angular/core';
import { TestcaseCreateComponent } from '../../testcase/testcase-create/testcase-create.component';

@Component({
  selector: 'app-admin-view',
  imports: [TestcaseCreateComponent],
  templateUrl: './admin-view.component.html',
  styleUrl: './admin-view.component.css'
})
export class AdminViewComponent {

}
